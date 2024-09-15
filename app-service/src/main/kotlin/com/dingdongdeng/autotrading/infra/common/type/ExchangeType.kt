package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeType(
    override val desc: String,
) : DescriptionType {
    UPBIT("업비트"),
    BINANCE("바이낸스"),
    ;

    companion object {
        fun of(name: String): ExchangeType {
            return entries.first { type -> type.name.equals(name, ignoreCase = true) }
        }
    }
}