package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeResult
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round
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
                coinTradeHistoryRepository.save(updateTradeHistory(notSyncedTradeHistory, order))
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
        keyPairId: String = "",
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
            summaries = tradeSummaries,
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
        val orderResponse = when {
            orderType.isBuy() || orderType.isSell() -> {
                val param = SpotCoinExchangeOrderParam(
                    coinType = coinType,
                    orderType = orderType,
                    volume = volume,
                    price = price,
                    priceType = priceType,
                )
                exchangeService.order(param, exchangeKeyPair)
            }

            orderType.isCancel() -> exchangeService.cancel(orderId!!, exchangeKeyPair)
            else -> throw CriticalException.of("확인되지 않은 주문 타입 orderType=$orderType")
        }

        // 취소 상태 업데이트
        if (orderResponse.orderType == OrderType.CANCEL) {
            val history = coinTradeHistoryRepository.findByOrderId(orderResponse.orderId)
            coinTradeHistoryRepository.save(history.cancel())
            return
        }

        // 매수, 매도 기록
        val tradeSummary = this.getTradeSummary(
            exchangeType = exchangeType,
            keyPairId = keyPairId,
            autoTradeProcessorId = autoTradeProcessorId,
            coinType = coinType,
            now = TimeContext.now(),
        )
        val profit =
            if (orderType.isSell()) ((orderResponse.price - tradeSummary.averagePrice) * orderResponse.volume).round() - orderResponse.fee.round() else 0.0
        coinTradeHistoryRepository.save(
            makeTradeHistory(
                order = orderResponse,
                autoTradeProcessorId = autoTradeProcessorId,
                profit = profit,
            )
        )
    }

    private fun updateTradeHistory(
        history: CoinTradeHistory,
        order: SpotCoinExchangeOrder,
    ): CoinTradeHistory {
        return CoinTradeHistory(
            id = history.id,
            orderId = order.orderId,
            state = order.tradeState,
            processorId = history.processorId,
            exchangeType = order.exchangeType,
            coinType = order.coinType,
            orderType = order.orderType,
            priceType = order.priceType,
            volume = order.volume,
            price = order.price,
            fee = order.fee,
            profit = history.profit,
            tradedAt = if (order.orderType == OrderType.CANCEL) order.cancelDateTime!! else order.orderDateTime!!,
        )
    }

    private fun makeTradeHistory(
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String,
        profit: Double,
    ): CoinTradeHistory {
        return CoinTradeHistory(
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
            profit = profit,
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