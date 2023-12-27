package com.dingdongdeng.autotrading.type

import java.util.EnumMap

enum class CoinExchangeType(
    val desc: String,
    val marketType: MarketType,
) {
    UPBIT("업비트", MarketType.SPOT),
    BINANCE_FUTURE("바이낸스 선물", MarketType.FUTURE);

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
