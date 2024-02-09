package com.dingdongdeng.autotrading.domain.chart.utils

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ChartUtils {

    fun findMissingCandles(candleUnit: CandleUnit, candleDateTimes: List<LocalDateTime>): List<LocalDateTime> {
        val missingCandles = mutableListOf<LocalDateTime>()
        candleDateTimes.zipWithNext { currentKst, nextKst ->
            val expectedDifference = candleUnit.getMinuteSize()
            val actualDifference = ChronoUnit.MINUTES.between(currentKst, nextKst)
            if (actualDifference != expectedDifference) {
                val missingCandleCount = (actualDifference / expectedDifference).toInt() - 1
                val missingCandlesInRange = (1..missingCandleCount).map {
                    currentKst.plusMinutes(expectedDifference * it)
                }
                missingCandles.addAll(missingCandlesInRange)
            }
        }
        return missingCandles
    }

    fun hasMissingCandle(candleUnit: CandleUnit, candleDateTimes: List<LocalDateTime>): Boolean {
        return findMissingCandles(candleUnit, candleDateTimes).isNotEmpty()
    }
}