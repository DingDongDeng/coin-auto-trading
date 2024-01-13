package com.dingdongdeng.autotrading.domain.exchange.utils

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import java.time.temporal.ChronoUnit

object ExchangeUtils {

    fun hasMissingCandle(candleUnit: CandleUnit, candles: List<ExchangeChartCandle>): Boolean {
        return candles.zipWithNext { current, next ->
            ChronoUnit.MINUTES.between(
                current.candleDateTimeKst,
                next.candleDateTimeKst
            ) != candleUnit.getMinuteSize()
        }.any { it }
    }
}