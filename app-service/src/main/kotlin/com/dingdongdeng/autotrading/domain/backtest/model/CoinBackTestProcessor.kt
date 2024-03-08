package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.LocalDateTime
import java.util.UUID

class CoinBackTestProcessor(
    override val id: String = "BACKTEST-${UUID.randomUUID()}",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격
    private val autoTradeProcessor: CoinAutoTradeProcessor,
) : Processor(
    id = id,
    userId = autoTradeProcessor.userId,
    duration = 0,
    slackSender = null,
) {

    private var initialize = false

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
}