package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeModeType(
    val desc: String,
) {
    PRODUCTION("운영"),
    BACKTEST("백테스트"),
    ;

    fun isProduction() = this == PRODUCTION
    fun isBackTest() = this == BACKTEST
}