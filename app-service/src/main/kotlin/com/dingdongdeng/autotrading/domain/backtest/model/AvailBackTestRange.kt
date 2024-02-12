package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.time.LocalDateTime

data class AvailBackTestRange(
    val exchangeType: ExchangeType,
    val candleUnit: CandleUnit,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
) {
    fun isRanged(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Boolean {
        return this.startDateTime <= startDateTime && this.endDateTime >= endDateTime
    }
}
