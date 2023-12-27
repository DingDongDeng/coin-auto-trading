package com.dingdongdeng.autotrading.type

enum class MarketType(
    val desc: String
) {
    SPOT("현물"),
    FUTURE("선물"),
    ;
}
