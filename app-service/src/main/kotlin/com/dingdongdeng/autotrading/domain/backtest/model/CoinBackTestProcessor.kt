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

        return merge(availBackTestRanges)
    }

    private fun merge(list: List<AvailBackTestRange>): List<AvailBackTestRange> {
        if (list.size <= 1) return list // 리스트 크기가 1 이하이면 그대로 반환

        var mergedList = list.sortedBy { it.startDateTime }.toMutableList() // 시작 시간을 기준으로 리스트 정렬

        var merged = true
        while (merged) {
            merged = false
            val newList = mutableListOf<AvailBackTestRange>()

            var i = 0
            while (i < mergedList.size - 1) {
                val currentRange = mergedList[i]
                val nextRange = mergedList[i + 1]

                // 현재 범위와 다음 범위의 시간 간격 계산
                val timeGap = Duration.between(currentRange.endDateTime, nextRange.startDateTime).toMinutes()

                if (timeGap <= 10) {
                    // 시간 간격이 3분 이내이면 범위를 병합
                    merged = true
                    mergedList.removeAt(i)
                    mergedList.removeAt(i)
                    mergedList.add(
                        i, AvailBackTestRange(
                        currentRange.exchangeType,
                        currentRange.coinType,
                        currentRange.startDateTime,
                        nextRange.endDateTime
                    )
                    )
                } else {
                    // 시간 간격이 3분보다 크면 현재 범위를 병합된 리스트에 추가하고 다음 범위를 현재 범위로 설정
                    newList.add(currentRange)
                    i++
                }
            }

            // 남은 마지막 요소 추가
            if (i == mergedList.size - 1) {
                newList.add(mergedList[i])
            }

            mergedList = newList
        }

        return mergedList
    }
}