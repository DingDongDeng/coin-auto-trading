package com.dingdongdeng.autotrading.domain.indicator.model


data class Ma(
    val sma10: Double,
    val sma20: Double,
    val sma60: Double,
    val sma120: Double,
    val sma200: Double,
    val ema60: Double,
)
