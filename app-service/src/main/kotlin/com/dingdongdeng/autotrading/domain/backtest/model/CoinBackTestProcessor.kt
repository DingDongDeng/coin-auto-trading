package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.minDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class CoinBackTestProcessor(
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격
    private val autoTradeProcessor: CoinAutoTradeProcessor,
) : Processor(
    id = autoTradeProcessor.id,
    userId = autoTradeProcessor.userId,
    duration = 0,
    slackSender = null,
) {

    private var now = startDateTime
    private var initialize = false

    override fun process() {
        autoTradeProcessor.process()
    }

    override fun runnable(): Boolean {
        if (initialize.not()) {
            TimeContext.update { startDateTime }
            initialize = true
        }
        val nextNow = minDate(TimeContext.now().plusSeconds(durationUnit.getSecondSize()), endDateTime)
        TimeContext.update { nextNow }
        now = nextNow
        return nextNow < endDateTime
    }

    fun progressRate(): Double {
        val totalDiff = abs(startDateTime.until(endDateTime, ChronoUnit.SECONDS))
        val progressDiff = abs(startDateTime.until(now, ChronoUnit.SECONDS))
        // xx.xx%
        return (progressDiff.toDouble() / totalDiff.toDouble()) * 100
    }

    fun now(): LocalDateTime = now
}