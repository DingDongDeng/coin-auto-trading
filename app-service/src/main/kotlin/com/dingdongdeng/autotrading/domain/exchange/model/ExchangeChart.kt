package com.dingdongdeng.autotrading.domain.exchange.model

import java.time.LocalDateTime

data class ExchangeChart(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Double,
    val candles: List<ExchangeChartCandle>,
    val missingCandles: List<LocalDateTime>,
)