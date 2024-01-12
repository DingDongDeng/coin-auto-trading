package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import org.springframework.stereotype.Component

@Component
class TestSpotCoinStrategy : SpotCoinStrategy {
    override fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): List<SpotCoinStrategyTask> {
        val param = params.first { it.coinType == CoinType.XRP }

        val charts = param.charts
        val tradeInfo = param.tradeInfo

        if (tradeInfo.existsWaitTrade()) {
            return tradeInfo.getOldWaitTrades(30L)
                .map {
                    SpotCoinStrategyTask(
                        coinType = it.coinType,
                        volume = it.volume,
                        price = it.price,
                        orderType = OrderType.CANCEL,
                        priceType = it.priceType,
                        orderId = it.orderId,
                    )
                }
        }

        if (tradeInfo.volume == 0.0) {
            return listOf(
                SpotCoinStrategyTask(
                    coinType = param.coinType,
                    volume = 7.0,
                    price = charts.first().currentPrice,
                    orderType = OrderType.BUY,
                    priceType = PriceType.LIMIT,
                )
            )
        }

        if (tradeInfo.volume == 7.0) {
            return listOf(
                SpotCoinStrategyTask(
                    coinType = param.coinType,
                    volume = 7.0,
                    price = charts.first().currentPrice,
                    orderType = OrderType.SELL,
                    priceType = PriceType.LIMIT,
                )
            )
        }

        return emptyList()
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.PROTO
    }
}