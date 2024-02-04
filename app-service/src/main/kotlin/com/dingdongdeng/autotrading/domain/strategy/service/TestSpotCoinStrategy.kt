package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class TestSpotCoinStrategy(
    private val objectMapper: ObjectMapper,
) : SpotCoinStrategySkeleton<TestSpotCoinStrategyConfig>() {

    override fun convertConfig(config: Map<String, Any>): TestSpotCoinStrategyConfig {
        log.info("start test ...")
        return objectMapper.convertValue(config, TestSpotCoinStrategyConfig::class.java)
    }

    override fun whenWaitTrades(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): Boolean {
        return params.any { it.tradeInfo.existsWaitTrade() }
    }

    override fun thenWaitTrades(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return params.flatMap { it.tradeInfo.getOldWaitTrades(30L) }
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
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenBuyTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun whenProfitTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenProfitTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun whenLossTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): Boolean {
        return false
    }

    override fun thenLossTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: TestSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return emptyList()
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.TEST
    }
}

data class TestSpotCoinStrategyConfig(
    val configA: String,
    val configB: Int,
)