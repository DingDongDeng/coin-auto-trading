package com.dingdongdeng.autotrading.domain.strategy.type

import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

enum class StrategyType(
    val desc: String,
    val exchangeType: ExchangeType
) {
    PROTO("프로토타입", ExchangeType.SPOT_COIN)
}