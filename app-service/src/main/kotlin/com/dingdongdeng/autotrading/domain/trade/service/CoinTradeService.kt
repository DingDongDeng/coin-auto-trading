package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeInfo
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import org.springframework.stereotype.Service

@Service
class CoinTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinTradeHistoryService: CoinTradeHistoryService,
) {

    fun getTradeInfo(
        exchangeType: ExchangeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinType: CoinType,
        currentPrice: Double,
    ): CoinTradeInfo {
        val notSyncedTradeHistories = coinTradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        val syncedTradeHistories = notSyncedTradeHistories.map { notSyncedTradeHistory ->
            // WAIT 상태의 거래건들 업데이트
            if (notSyncedTradeHistory.state == TradeState.WAIT) {
                val exchangeService = exchangeServices.first { it.support(exchangeType) }
                val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
                val order = exchangeService.getOrder(notSyncedTradeHistory.orderId, exchangeKeyPair)
                coinTradeHistoryService.record(makeTradeHistory(notSyncedTradeHistory.id, order, autoTradeProcessorId))
            } else {
                notSyncedTradeHistory
            }
        }

        val buyTradeHistories = syncedTradeHistories.filter { it.isBuyOrder() }
        val sellTradeHistories = syncedTradeHistories.filter { it.isSellOrder() }

        val volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume }
        val value = buyTradeHistories.sumOf { it.price * it.volume } - sellTradeHistories.sumOf { it.price * it.volume }
        val averagePrice = if (volume == 0.0) 0.0 else (value / volume)
        val valuePrice = (volume * currentPrice)
        val originPrice = (volume * averagePrice)

        //FIXME 승률도 넣으면 좋을듯

        return CoinTradeInfo(
            volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume },
            averagePrice = averagePrice,
            valuePrice = valuePrice,
            originPrice = originPrice,
            profitPrice = (valuePrice - originPrice),
            coinTradeHistory = syncedTradeHistories,
        )
    }

    fun trade(
        orderId: String?,
        autoTradeProcessorId: String,
        exchangeType: ExchangeType,
        keyPairId: String,
        orderType: OrderType,
        coinType: CoinType,
        volume: Double,
        price: Double,
        priceType: PriceType,
    ) {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        val orderResponse = when (orderType) {
            OrderType.BUY, OrderType.SELL -> {
                val param = SpotCoinExchangeOrderParam(
                    coinType = coinType,
                    orderType = orderType,
                    volume = volume,
                    price = price,
                    priceType = priceType,
                )
                exchangeService.order(param, exchangeKeyPair)
            }

            OrderType.CANCEL -> exchangeService.cancel(orderId!!, exchangeKeyPair)
        }

        // 취소 상태 업데이트
        if (orderResponse.orderType == OrderType.CANCEL) {
            coinTradeHistoryService.cancel(orderResponse.orderId)
            return
        }

        // 매수, 매도 기록
        coinTradeHistoryService.record(
            makeTradeHistory(
                order = orderResponse,
                autoTradeProcessorId = autoTradeProcessorId
            )
        )
    }

    private fun makeTradeHistory(
        coinTradehistoryId: Long? = null,
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String
    ): CoinTradeHistory {
        return CoinTradeHistory(
            id = coinTradehistoryId,
            orderId = order.orderId,
            state = order.tradeState,
            processorId = autoTradeProcessorId,
            exchangeType = order.exchangeType,
            coinType = order.coinType,
            orderType = order.orderType,
            priceType = order.priceType,
            volume = order.volume,
            price = order.price,
            fee = order.fee,
            tradedAt = if (order.orderType == OrderType.CANCEL) order.cancelDateTime!! else order.orderDateTime!!,
        )
    }
}