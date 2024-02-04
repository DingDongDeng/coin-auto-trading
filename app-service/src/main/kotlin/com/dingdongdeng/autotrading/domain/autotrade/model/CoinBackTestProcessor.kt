package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.LocalDateTime
import java.util.UUID

class CoinBackTestProcessor(
    override val id: String = "BACKTEST-${UUID.randomUUID()}",
    override val userId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격
    val autoTradeProcessor: Processor,
) : Processor(
    id = id,
    userId = userId,
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
        val now = TimeContext.now().plusMinutes(durationUnit.getMinuteSize())
        TimeContext.update { now }
        return now.isBefore(endDateTime)
    }
}