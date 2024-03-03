package com.dingdongdeng.autotrading.domain.strategy.component.impl

import com.dingdongdeng.autotrading.domain.strategy.component.SimpleSpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round
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
        return param.tradeInfo.existsWaitTrade
    }

    override fun thenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return param.tradeInfo.getOldWaitTrades(30L)
            .map {
                SpotCoinStrategyTask.ofCancel(
                    orderId = it.orderId,
                    coinType = it.coinType,
                    volume = it.volume,
                    price = it.price,
                    priceType = it.priceType,
                )
            }
    }

    override fun whenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        if (param.tradeInfo.hasVolume) {
            return false
        }
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        val rsi = chart15M.last(2).indicators.rsi
        return rsi < 0.3
    }

    override fun thenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        return listOf(
            SpotCoinStrategyTask.ofBuyLimit(
                coinType = param.coinType,
                volume = (config.onceTradeAmount / chart15M.currentPrice).round(4.0),
                price = chart15M.currentPrice,
            )
        )
    }

    override fun whenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        if (param.tradeInfo.profitRate <= 0) {
            return false
        }
        if (chart15M.last(1).indicators.rsi < 0.65) {
            return false
        }
        return true
    }

    override fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return listOf(
            SpotCoinStrategyTask.ofSellLimit(
                coinType = param.coinType,
                volume = param.tradeInfo.volume,
                price = param.tradeInfo.currentPrice,
            )
        )
    }

    override fun whenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        if (param.tradeInfo.profitRate >= 0) {
            return false
        }
        if (chart15M.last(1).indicators.rsi > 0.30) {
            return false
        }
        return true
    }

    override fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return listOf(
            SpotCoinStrategyTask.ofSellLimit(
                coinType = param.coinType,
                volume = param.tradeInfo.volume,
                price = param.tradeInfo.currentPrice,
            )
        )
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.EXAMPLE
    }
}

data class ExampleSpotCoinStrategyConfig(
    val onceTradeAmount: Int,
)