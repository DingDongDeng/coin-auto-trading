package com.dingdongdeng.autotrading.infra.common.utils

import kotlin.math.pow

fun Double.round(digit: Double = 0.0): Double {
    val multiplier = 10.0.pow(digit)
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Double.ceil(digit: Double = 0.0): Double {
    val multiplier = 10.0.pow(digit)
    return kotlin.math.ceil(this * multiplier) / multiplier
}

fun Double.floor(digit: Double = 0.0): Double {
    val multiplier = 10.0.pow(digit)
    return kotlin.math.floor(this * multiplier) / multiplier
}