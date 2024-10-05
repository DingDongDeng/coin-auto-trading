package com.dingdongdeng.autotrading.domain.indicator.model


data class Ma(
    private val sma10Func: () -> Double,
    private val sma20Func: () -> Double,
    private val sma60Func: () -> Double,
    private val sma120Func: () -> Double,
    private val sma200Func: () -> Double,
    private val ema60Func: () -> Double,
) {
    val sma10 by lazy(sma10Func)
    val sma20 by lazy(sma20Func)
    val sma60 by lazy(sma60Func)
    val sma120 by lazy(sma120Func)
    val sma200 by lazy(sma200Func)
    val ema60 by lazy(ema60Func)
}
