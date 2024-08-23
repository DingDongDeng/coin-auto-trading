package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeType(
    override val desc: String,
    val asReal: ExchangeType,
    val backTest: Boolean = false,
) : DescriptionType {
    UPBIT("업비트", UPBIT),
    BINANCE("바이낸스", BINANCE),
    BACKTEST_UPBIT("업비트 백테스트", UPBIT, backTest = true)
    ;

    companion object {
        fun of(name: String): ExchangeType {
            return entries.first { type -> type.name.equals(name, ignoreCase = true) }
        }

        fun ofNotBackTest(): List<ExchangeType> {
            return entries.filter { it.backTest.not() }
        }
    }
}