package com.dingdongdeng.autotrading.type

enum class OrderType(
    val desc: String,
) {
    BUY("매수"),
    SELL("매도"),
    CANCEL("주문 취소");
}
