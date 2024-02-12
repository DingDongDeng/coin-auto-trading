package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.service.CoinStrategyService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class CoinBackTestProcessor(
    override val id: String = "BACKTEST-${UUID.randomUUID()}",
    override val userId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격

    val coinStrategyType: CoinStrategyType,
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val config: Map<String, Any>,
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
    private val coinStrategyService: CoinStrategyService,
) : Processor(
    id = id,
    userId = userId,
    duration = 0,
    slackSender = null,
) {
    private val autoTradeProcessor: Processor = CoinAutoTradeProcessor(
        id = id,
        userId = userId,
        coinStrategyType = coinStrategyType,
        exchangeType = ExchangeType.BACKTEST,
        coinTypes = coinTypes,
        candleUnits = candleUnits,
        keyPairId = "",
        config = config,
        duration = 0,
        slackSender = null,
        coinChartService = coinChartService,
        coinTradeService = coinTradeService,
        coinStrategyService = coinStrategyService,
    )
    private var initialize = false

    init {
        validateBackTestRange()
    }

    override fun process() {
        autoTradeProcessor.process()
    }

    override fun runnable(): Boolean {
        if (initialize.not()) {
            TimeContext.update { startDateTime }
            initialize = true
        }
        val now = TimeContext.now().plusSeconds(durationUnit.getSecondSize())
        TimeContext.update { now }
        return now < endDateTime
    }

    private fun validateBackTestRange() {
        val availBackTestRanges = coinTypes.flatMap { getAvailBackTestRanges(it) }
        coinTypes.forEach { coinType ->
            if (availBackTestRanges.none { it.isRanged(coinType, startDateTime, endDateTime) }) {
                throw WarnException.of("백테스트 불가능한 구간입니다. availBackTestRanges=$availBackTestRanges")
            }
        }
    }

    private fun getAvailBackTestRanges(coinType: CoinType): List<AvailBackTestRange> {
        val minUnit = CandleUnit.min()
        val missingCandles = coinChartService.getMissingChart(
            exchangeType = exchangeType,
            coinType = coinType,
            candleUnit = minUnit,
            from = startDateTime,
            to = endDateTime,
        ).candles

        val availBackTestRanges = mutableListOf<AvailBackTestRange>()
        missingCandles.windowed(2, 1) { subList ->
            val firstMissingDateTime = subList.first().candleDateTimeKst
            val lastMissingDateTime = subList.last().candleDateTimeKst

            // 누락된 캔들이 연속된 시간에 존재하면 생략
            if (firstMissingDateTime.plusSeconds(minUnit.getSecondSize()) >= lastMissingDateTime) {
                return@windowed
            }

            availBackTestRanges.add(
                AvailBackTestRange(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    startDateTime = firstMissingDateTime.plusSeconds(minUnit.getSecondSize()),
                    endDateTime = lastMissingDateTime.minusSeconds(minUnit.getSecondSize()),
                )
            )
        }

        if (availBackTestRanges.first().startDateTime != startDateTime) {
            availBackTestRanges.add(
                0,
                AvailBackTestRange(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    startDateTime = startDateTime,
                    endDateTime = missingCandles.first().candleDateTimeKst.minusSeconds(minUnit.getSecondSize()),
                )
            )
        }

        if (availBackTestRanges.last().endDateTime != endDateTime) {
            availBackTestRanges.add(
                0,
                AvailBackTestRange(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    startDateTime = missingCandles.last().candleDateTimeKst.plusSeconds(minUnit.getSecondSize()),
                    endDateTime = endDateTime,
                )
            )
        }

        return merge(availBackTestRanges)
    }

    private fun merge(list: List<AvailBackTestRange>): List<AvailBackTestRange> {
        if (list.size <= 1) return list // 리스트 크기가 1 이하이면 그대로 반환

        return list.sortedBy { it.startDateTime }.fold(emptyList()) { acc, range ->
            if (acc.isEmpty()) {
                listOf(range)
            } else {
                val lastRange = acc.last()
                val timeGap = Duration.between(lastRange.endDateTime, range.startDateTime).toMinutes()
                if (timeGap <= 10) {
                    acc.dropLast(1) + AvailBackTestRange(
                        lastRange.exchangeType,
                        lastRange.coinType,
                        lastRange.startDateTime,
                        range.endDateTime
                    )
                } else {
                    acc + range
                }
            }
        }
    }
}