package com.dingdongdeng.autotrading.domain.chart.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import java.time.LocalDateTime

data class Chart(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Double,
    val candleUnit: CandleUnit,
    val candles: List<Candle>,
) {
    fun last(n: Int): Candle = candles[candles.size - 1 - n]
}