package com.dingdongdeng.autotrading.infra.common.type

import java.util.EnumMap

enum class ExchangeType(
    val desc: String,
) {
    UPBIT("업비트"),
    BINANCE("바이낸스");

    companion object {
        fun of(name: String?): ExchangeType {
            return values().first { type -> type.name.equals(name, ignoreCase = true) }
        }

        fun toMap(): EnumMap<ExchangeType, String> {
            val map = EnumMap<ExchangeType, String>(
                ExchangeType::class.java
            )
            for (value in values()) {
                map[value] = value.desc
            }
            return map
        }
    }
}
