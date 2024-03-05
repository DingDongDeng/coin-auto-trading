package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.LocalDateTime
import java.util.UUID

class CoinBackTestProcessor(
    override val id: String = "BACKTEST-${UUID.randomUUID()}",
    override val userId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격

    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val config: Map<String, Any>,
    private val strategy: SpotCoinStrategy,
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
) : Processor(
    id = id,
    userId = userId,
    duration = 0,
    slackSender = null,
) {
    private val autoTradeProcessor: Processor = CoinAutoTradeProcessor(
        id = id,
        userId = userId,
        exchangeType = ExchangeType.BACKTEST,
        coinTypes = coinTypes,
        candleUnits = candleUnits,
        keyPairId = "",
        config = config,
        duration = 0,
        slackSender = null,
        strategy = strategy,
        coinChartService = coinChartService,
        coinTradeService = coinTradeService,
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
        val missingDateTimes = coinChartService.getMissingChart(
            exchangeType = exchangeType,
            coinType = coinType,
            candleUnit = minUnit,
            from = startDateTime,
            to = endDateTime,
        ).candles.map { it.candleDateTimeKst }

        val availBackTestRanges = AvailBackTestRange.fromMissingDateTimes(
            exchangeType = exchangeType,
            coinType = coinType,
            candleUnit = minUnit,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            missingDateTimes = missingDateTimes,
        )

        return AvailBackTestRange.merge(availBackTestRanges, minUnit.getSecondSize() * 10)
    }
}