package com.dingdongdeng.autotrading.domain.exchange.model

import java.time.LocalDateTime

data class ExchangeChart(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val candles: List<ExchangeChartCandle>,
) {
    val currentPrice: Double = if (candles.isEmpty()) 0.0 else candles.last().closingPrice
}