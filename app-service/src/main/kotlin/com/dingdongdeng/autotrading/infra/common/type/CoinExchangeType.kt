package com.dingdongdeng.autotrading.infra.common.type

import java.util.EnumMap

enum class CoinExchangeType(
    val desc: String,
    val exchangeTypes: List<ExchangeType>,
) {
    UPBIT(
        "업비트",
        listOf(ExchangeType.SPOT_COIN)
    ),
    BINANCE_FUTURE(
        "바이낸스 선물",
        listOf(ExchangeType.SPOT_COIN, ExchangeType.FUTURE_COIN)
    );

    companion object {
        fun of(name: String?): CoinExchangeType {
            return values().first { type -> type.name.equals(name, ignoreCase = true) }
        }

        fun toMap(): EnumMap<CoinExchangeType, String> {
            val map = EnumMap<CoinExchangeType, String>(
                CoinExchangeType::class.java
            )
            for (value in values()) {
                map[value] = value.desc
            }
            return map
        }
    }
}
