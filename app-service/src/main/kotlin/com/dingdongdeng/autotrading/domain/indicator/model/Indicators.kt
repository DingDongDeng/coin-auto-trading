package com.dingdongdeng.autotrading.domain.indicator.model

import java.time.LocalDateTime

data class Indicators(
    val indicatorDateTimeKst: LocalDateTime,
    val rsi: Double,
    val macd: Macd,
    val bollingerBands: BollingerBands,
    val obv: Obv,
    val ma: Ma,
)
