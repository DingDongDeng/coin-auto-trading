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
    // 마지막 N번째 요소 (1이 마지막 요소)
    fun getLast(cnt: Int): Candle = candles[candles.size - cnt]
}