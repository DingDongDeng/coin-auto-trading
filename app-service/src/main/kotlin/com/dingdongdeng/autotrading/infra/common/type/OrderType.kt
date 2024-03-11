package com.dingdongdeng.autotrading.infra.common.type

enum class OrderType(
    val desc: String,
) {
    BUY("매수"),
    SELL("매도"),
    CANCEL("주문 취소");

    fun isBuy(): Boolean = this == BUY
    fun isSell(): Boolean = this == SELL
    fun isCancel(): Boolean = this == CANCEL
}
