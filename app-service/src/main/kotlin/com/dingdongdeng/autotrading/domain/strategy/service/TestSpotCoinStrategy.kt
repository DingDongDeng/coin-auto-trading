package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class TestSpotCoinStrategy(
    private val objectMapper: ObjectMapper,
) : SpotCoinStrategy {
    var count = 0
    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        if (count++ % 100 != 0) {
            return emptyList()
        }

        log.info("test ...")
        val param = params.first()
        val config = objectMapper.convertValue(config, TestSpotCoinStrategyConfig::class.java)

        val currentPrice = param.getChart(CandleUnit.UNIT_15M).currentPrice
        val charts = param.charts
        val tradeInfo = param.tradeInfo
        val indicators = charts.first().candles.first().getIndicators()

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
                    price = currentPrice,
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
                    price = currentPrice,
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

data class TestSpotCoinStrategyConfig(
    val configA: String,
    val configB: Int,
)