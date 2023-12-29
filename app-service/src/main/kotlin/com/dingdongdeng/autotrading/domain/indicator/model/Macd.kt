package com.dingdongdeng.autotrading.domain.indicator.model

data class Macd(
    val hist: Double,
    val signal: Double,
    val macd: Double,
)