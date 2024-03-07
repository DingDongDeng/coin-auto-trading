package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeType(
    val desc: String,
) {
    UPBIT("업비트"),
    BINANCE("바이낸스"),
    BACKTEST_UPBIT("업비트 백테스트")
    ;

    companion object {
        fun of(name: String): ExchangeType {
            return entries.first { type -> type.name.equals(name, ignoreCase = true) }
        }
    }
}
