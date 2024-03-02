package com.dingdongdeng.autotrading.domain.strategy.component.impl

import com.dingdongdeng.autotrading.domain.strategy.component.SimpleSpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ExampleSpotCoinStrategy(
    private val objectMapper: ObjectMapper,
) : SimpleSpotCoinStrategy<ExampleSpotCoinStrategyConfig>() {

    override fun convertConfig(config: Map<String, Any>): ExampleSpotCoinStrategyConfig {
        log.info("start test ... ${TimeContext.now()}")
        return objectMapper.convertValue(config, ExampleSpotCoinStrategyConfig::class.java)
    }

    override fun whenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        return param.tradeInfo.existsWaitTrade()
    }

    override fun thenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return param.tradeInfo.getOldWaitTrades(30L)
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

    override fun whenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun whenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun whenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.EXAMPLE
    }
}

data class ExampleSpotCoinStrategyConfig(
    val configA: String,
    val configB: Int,
)