package com.dingdongdeng.autotrading.infra.common.type

enum class MarketType(
    val desc: String
) {
    SPOT("현물"),
    FUTURE("선물"),
    ;
}
