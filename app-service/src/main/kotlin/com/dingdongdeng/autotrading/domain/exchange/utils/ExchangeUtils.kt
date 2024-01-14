package com.dingdongdeng.autotrading.domain.exchange.utils

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ExchangeUtils {

    fun hasMissingCandle(candleUnit: CandleUnit, candleDateTimes: List<LocalDateTime>): Boolean {
        return candleDateTimes.zipWithNext { currentKst, nextKst ->
            ChronoUnit.MINUTES.between(currentKst, nextKst) != candleUnit.getMinuteSize()
        }.any { it }
    }
}