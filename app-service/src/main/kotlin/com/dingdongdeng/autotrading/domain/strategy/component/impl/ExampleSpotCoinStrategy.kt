package com.dingdongdeng.autotrading.domain.strategy.component.impl

import com.dingdongdeng.autotrading.domain.strategy.component.SimpleSpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ExampleSpotCoinStrategy(
    private val objectMapper: ObjectMapper,
) : SimpleSpotCoinStrategy<ExampleSpotCoinStrategyConfig>() {

    private var accProfit = 0.0
    override fun convertConfig(config: Map<String, Any>): ExampleSpotCoinStrategyConfig {
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
        if (param.tradeInfo.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }

        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        val rsi = chart15M.last(1).indicators.rsi
        return rsi < 0.3
    }

    override fun thenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        log(
            "매수",
            param.coinType,
            (config.onceTradeAmount / chart15M.currentPrice),
            param.tradeInfo.currentPrice,
            0.0
        )
        return listOf(
            SpotCoinStrategyTask.ofBuyLimit(
                coinType = param.coinType,
                volume = (config.onceTradeAmount / chart15M.currentPrice).round(4.0),
                price = param.tradeInfo.currentPrice,
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
        if (param.tradeInfo.profitRate <= 8.0) {
            return false
        }
        if (param.tradeInfo.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }
        return true
    }

    override fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        log("익절", param.coinType, param.tradeInfo.volume, param.tradeInfo.currentPrice, param.tradeInfo.profitPrice)
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
        if (param.tradeInfo.profitRate >= -5.0) {
            return false
        }
        if (param.tradeInfo.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }
        return true
    }

    override fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        log("손절", param.coinType, param.tradeInfo.volume, param.tradeInfo.currentPrice, param.tradeInfo.profitPrice)
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

    private fun log(tag: String, coinType: CoinType, volume: Double, price: Double, profitPrice: Double) {
        accProfit += profitPrice
        val accProfitStr = accProfit.round().toString().padStart(15, '0')
        log.info(
            "[$tag] [$accProfitStr] ${TimeContext.now()} / $coinType / ${volume.round(4.0)} / ${price.round()} / ${profitPrice.round()}"
        )
    }
}

data class ExampleSpotCoinStrategyConfig(
    val onceTradeAmount: Int,
)