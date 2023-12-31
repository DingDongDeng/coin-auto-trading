package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType

data class SpotCoinStrategyMakeTaskResult(
    val coinType: CoinType,
    val volume: Double,
    val price: Double,
    val orderType: OrderType,
    val priceType: PriceType,
)
