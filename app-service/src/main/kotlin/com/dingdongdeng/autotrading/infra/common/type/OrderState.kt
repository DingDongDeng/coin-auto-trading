package com.dingdongdeng.autotrading.infra.common.type

enum class OrderState(
    val desc: String
) {
    WAIT("체결 대기"),
    WATCH("예약 주문 대기"),
    DONE("체결 완료"),
    CANCEL("주문 취소"),
    LIQUIDATION("주문 청산");
}
