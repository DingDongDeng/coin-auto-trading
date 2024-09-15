package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByCountParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeResult
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeResultDetail
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeStatistics
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.infra.common.utils.LocalDateUtils
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.atEndOfMonth
import com.dingdongdeng.autotrading.infra.common.utils.atStartOfMonth
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class CoinTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun syncTradeHistories(
        exchangeType: ExchangeType,
        exchangeModeType: ExchangeModeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinType: CoinType,
    ): List<CoinTradeHistory> {
        val exchangeService = exchangeServices.first { it.support(exchangeType, exchangeModeType) }
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

    fun getTradeResult(
        exchangeType: ExchangeType,
        exchangeModeType: ExchangeModeType,
        keyPairId: String,
        processorId: String,
        coinTypes: List<CoinType>,
        now: LocalDateTime,
    ): CoinTradeResult {
        val tradeDetails = coinTypes.map { coinType ->
            val summary = this.getTradeSummary(
                exchangeType = exchangeType,
                exchangeModeType = exchangeModeType,
                keyPairId = keyPairId,
                autoTradeProcessorId = processorId,
                coinType = coinType,
                now = now,
            )

            val statistics = this.getTradeMonthlyStats(
                exchangeType = exchangeType,
                processorId = processorId,
                coinType = coinType,
                fromMonth = if (summary.tradeHistories.isEmpty()) now.atStartOfMonth() else summary.tradeHistories.first().tradedAt.atStartOfMonth(),
                toMonth = now.atEndOfMonth(),
            )

            CoinTradeResultDetail(
                summary = summary,
                statistics = statistics,
            )
        }

        return CoinTradeResult(
            now = now,
            processorId = processorId,
            details = tradeDetails,
        )
    }

    fun getTradeSummary(
        exchangeType: ExchangeType,
        exchangeModeType: ExchangeModeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        coinType: CoinType,
        now: LocalDateTime,
    ): CoinTradeSummary {
        val tradeHistories = coinTradeHistoryRepository
            .findAllCoinTradeHistories(autoTradeProcessorId, coinType)
            .filter { it.tradedAt <= now }
        val exchangeService = exchangeServices.first { it.support(exchangeType, exchangeModeType) }
        val keyPair = exchangeService.getKeyPair(keyPairId)
        val chart = exchangeService.getChartByCount(
            param = SpotCoinExchangeChartByCountParam(
                coinType = coinType,
                candleUnit = CandleUnit.min(),
                to = now,
                count = 1,
            ),
            keyParam = keyPair
        )
        return CoinTradeSummary(
            coinType = coinType,
            now = now,
            processorId = autoTradeProcessorId,
            currentPrice = chart.candles.last().closingPrice,
            tradeHistories = tradeHistories,
        )
    }

    fun getTradeMonthlyStats(
        exchangeType: ExchangeType,
        processorId: String,
        coinType: CoinType,
        fromMonth: LocalDate,
        toMonth: LocalDate,
    ): List<CoinTradeStatistics> {
        val tradeHistories = coinTradeHistoryRepository
            .findAllCoinTradeHistories(processorId, coinType)
            .filter { it.tradedAt.toLocalDate() >= fromMonth && it.tradedAt.toLocalDate() <= toMonth }
        val monthlyHistoriesMap = tradeHistories.groupBy { Pair(it.tradedAt.year, it.tradedAt.month.value) }
        return monthlyHistoriesMap.map { monthlyHistories ->
            val year = monthlyHistories.key.first
            val month = monthlyHistories.key.second
            val histories = monthlyHistories.value

            CoinTradeStatistics(
                coinType = coinType,
                from = LocalDateUtils.atStartOfMonth(year, month),
                to = LocalDateUtils.atEndOfMonth(year, month),
                processorId = processorId,
                tradeHistories = histories,
            )
        }
    }

    fun trade(
        orderId: String?,
        autoTradeProcessorId: String,
        exchangeType: ExchangeType,
        exchangeModeType: ExchangeModeType,
        keyPairId: String,
        orderType: OrderType,
        coinType: CoinType,
        volume: Double,
        price: Double,
        priceType: PriceType,
    ) {
        val exchangeService = exchangeServices.first { it.support(exchangeType, exchangeModeType) }
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
            exchangeModeType = exchangeModeType,
            keyPairId = keyPairId,
            autoTradeProcessorId = autoTradeProcessorId,
            coinType = coinType,
            now = TimeContext.now(),
        )
        val profit =
            if (orderType.isSell()) ((orderResponse.price - tradeSummary.averagePrice) * orderResponse.volume) - orderResponse.fee else 0.0
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
}