package com.dingdongdeng.autotrading.infra.common.type

enum class TradeState(
    val desc: String
) {
    WAIT("체결 대기"),
    DONE("체결 완료"),
    CANCEL("주문 취소"),
    ;

    fun isWait(): Boolean = this == WAIT
    fun isDone(): Boolean = this == DONE
}
