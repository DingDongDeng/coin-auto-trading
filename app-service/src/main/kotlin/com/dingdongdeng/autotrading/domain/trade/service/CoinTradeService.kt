package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeResult
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun syncTradeHistories(
        exchangeType: ExchangeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinType: CoinType,
    ): List<CoinTradeHistory> {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        val notSyncedTradeHistories =
            coinTradeHistoryRepository.findAllCoinTradeHistories(autoTradeProcessorId, coinType)
        val syncedTradeHistories = notSyncedTradeHistories.map { notSyncedTradeHistory ->
            // WAIT 상태의 거래건들 업데이트
            if (notSyncedTradeHistory.isWait()) {
                val order = exchangeService.getOrder(notSyncedTradeHistory.orderId, exchangeKeyPair)
                coinTradeHistoryRepository.save(makeTradeHistory(notSyncedTradeHistory.id, order, autoTradeProcessorId))
            } else {
                notSyncedTradeHistory
            }
        }
        return syncedTradeHistories
    }

    fun getTradeSummary(
        exchangeType: ExchangeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinType: CoinType,
        now: LocalDateTime,
    ): CoinTradeSummary {
        val tradeHistories = coinTradeHistoryRepository
            .findAllCoinTradeHistories(autoTradeProcessorId, coinType)
            .filter { it.tradedAt <= now }
        val currentPrice = getCurrentPrice(
            exchangeType = exchangeType,
            keyPairId = keyPairId,
            coinType = coinType,
            now = now,
        )
        return CoinTradeSummary(
            now = now,
            processorId = autoTradeProcessorId,
            currentPrice = currentPrice,
            tradeHistories = tradeHistories,
        )
    }

    fun getTradeResult(
        exchangeType: ExchangeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinTypes: List<CoinType>,
        now: LocalDateTime,
    ): CoinTradeResult {
        val tradeSummaries = coinTypes.map { coinType ->
            this.getTradeSummary(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                autoTradeProcessorId = autoTradeProcessorId,
                coinType = coinType,
                now = now,
            )
        }
        return CoinTradeResult(
            now = now,
            processorId = autoTradeProcessorId,
            tradeSummaries = tradeSummaries,
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
            val history = coinTradeHistoryRepository.findByOrderId(orderResponse.orderId)
            coinTradeHistoryRepository.save(history.cancel())
            return
        }

        // 매수, 매도 기록
        coinTradeHistoryRepository.save(
            makeTradeHistory(
                order = orderResponse,
                autoTradeProcessorId = autoTradeProcessorId
            )
        )
    }

    private fun makeTradeHistory(
        coinTradeHistoryId: Long? = null,
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String
    ): CoinTradeHistory {
        return CoinTradeHistory(
            id = coinTradeHistoryId,
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

    private fun getCurrentPrice(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        now: LocalDateTime
    ): Double {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val keyPair = exchangeService.getKeyPair(keyPairId)
        val chart = exchangeService.getChart(
            param = SpotCoinExchangeChartParam(
                coinType = coinType,
                candleUnit = CandleUnit.min(),
                from = now.minusSeconds(CandleUnit.min().getSecondSize() * 2),
                to = now,
            ),
            keyParam = keyPair
        )
        return chart.currentPrice
    }
}