package com.dingdongdeng.autotrading.domain.exchange.model

import java.time.LocalDateTime

data class ExchangeChart(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val candles: List<ExchangeChartCandle>,
)