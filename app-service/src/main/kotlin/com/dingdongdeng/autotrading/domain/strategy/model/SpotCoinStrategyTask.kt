package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType

data class SpotCoinStrategyTask(
    val coinType: CoinType,
    val volume: Double,
    val price: Double,
    val orderType: OrderType,
    val priceType: PriceType,
    val orderId: String? = null, // 취소의 경우 사용
) {
    companion object {
        fun ofLimitBuy(coinType: CoinType, volume: Double, price: Double): SpotCoinStrategyTask {
            return SpotCoinStrategyTask(
                coinType = coinType,
                volume = volume,
                price = price,
                orderType = OrderType.BUY,
                priceType = PriceType.LIMIT,
            )
        }

        fun ofLimitSell(coinType: CoinType, volume: Double, price: Double): SpotCoinStrategyTask {
            return SpotCoinStrategyTask(
                coinType = coinType,
                volume = volume,
                price = price,
                orderType = OrderType.SELL,
                priceType = PriceType.LIMIT,
            )
        }

        fun ofCancel(
            orderId: String,
            coinType: CoinType,
            volume: Double,
            price: Double,
            priceType: PriceType
        ): SpotCoinStrategyTask {
            return SpotCoinStrategyTask(
                orderId = orderId,
                coinType = coinType,
                volume = volume,
                price = price,
                orderType = OrderType.CANCEL,
                priceType = priceType,
            )

        }
    }
}
