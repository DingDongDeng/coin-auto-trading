package com.dingdongdeng.autotrading.domain.indicator.model

import java.time.LocalDateTime

data class Indicators(
    private val indicatorDateTimeKst: LocalDateTime,
    private val rsiFunc: () -> Double,
    private val macdFunc: () -> Macd,
    private val bollingerBandsFunc: () -> BollingerBands,
    private val obvFunc: () -> Obv,
    private val maFunc: () -> Ma,
) {
    val rsi by lazy(rsiFunc)
    val macd by lazy(macdFunc)
    val bollingerBands by lazy(bollingerBandsFunc)
    val obv by lazy(obvFunc)
    val ma by lazy(maFunc)
}
