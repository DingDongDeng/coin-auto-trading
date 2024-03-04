package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeInfo
import com.dingdongdeng.autotrading.infra.common.annotation.DomainService
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType

@DomainService
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
            if (notSyncedTradeHistory.isWait()) {
                val exchangeService = exchangeServices.first { it.support(exchangeType) }
                val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
                val order = exchangeService.getOrder(notSyncedTradeHistory.orderId, exchangeKeyPair)
                coinTradeHistoryService.save(makeTradeHistory(notSyncedTradeHistory.id, order, autoTradeProcessorId))
            } else {
                notSyncedTradeHistory
            }
        }

        return CoinTradeInfo(
            currentPrice = currentPrice,
            tradeHistories = syncedTradeHistories,
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
            val history = coinTradeHistoryService.findTradeHistory(orderResponse.orderId)
            coinTradeHistoryService.save(history.cancel())
            return
        }

        // 매수, 매도 기록
        coinTradeHistoryService.save(
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