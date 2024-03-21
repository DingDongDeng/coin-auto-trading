package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeType(
    override val desc: String,
    val asReal: ExchangeType,
) : DescriptionType {
    UPBIT("업비트", UPBIT),
    BINANCE("바이낸스", BINANCE),
    BACKTEST_UPBIT("업비트 백테스트", UPBIT)
    ;

    companion object {
        fun of(name: String): ExchangeType {
            return entries.first { type -> type.name.equals(name, ignoreCase = true) }
        }
    }
}