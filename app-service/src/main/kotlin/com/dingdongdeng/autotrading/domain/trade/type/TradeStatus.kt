package com.dingdongdeng.autotrading.domain.trade.type

enum class TradeStatus(
    val desc: String,
) {
    WAIT("대기"),
    DONE("완료"),
    CANCEL("취소");
}
