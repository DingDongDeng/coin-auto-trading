package com.dingdongdeng.autotrading.infra.common.type

enum class ExchangeType(
    val desc: String,
    val marketType: MarketType,
) {
    SPOT_COIN("현물 코인 거래소", MarketType.SPOT),
    FUTURE_COIN("선물 코인 거래소", MarketType.FUTURE);
}
