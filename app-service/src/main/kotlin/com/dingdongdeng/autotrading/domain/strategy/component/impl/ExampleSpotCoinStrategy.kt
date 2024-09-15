package com.dingdongdeng.autotrading.domain.strategy.component.impl

import com.dingdongdeng.autotrading.domain.strategy.component.SimpleSpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.component.annotation.GuideDescription
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
        return param.tradeSummary.existsWaitTrade
    }

    override fun thenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return param.tradeSummary.getOldWaitTrades(30L)
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
        if (param.tradeSummary.hasVolume) {
            return false
        }
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }

        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        val rsi = chart15M.getLast(1).indicators.rsi
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
            (config.onceTradeAmount / param.currentPrice),
            param.tradeSummary.currentPrice,
            0.0
        )
        return listOf(
            SpotCoinStrategyTask.ofBuyLimit(
                coinType = param.coinType,
                volume = (config.onceTradeAmount / param.currentPrice).round(4.0),
                price = param.tradeSummary.currentPrice,
            )
        )
    }

    override fun whenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        if (param.tradeSummary.profitRate <= 0) {
            return false
        }
        if (chart15M.getLast(1).indicators.rsi < 0.65) {
            return false
        }
        if (param.tradeSummary.profitRate <= 8.0) {
            return false
        }
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }
        return true
    }

    override fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        log(
            "익절",
            param.coinType,
            param.tradeSummary.volume,
            param.tradeSummary.currentPrice,
            param.tradeSummary.profitPrice
        )
        return listOf(
            SpotCoinStrategyTask.ofSellLimit(
                coinType = param.coinType,
                volume = param.tradeSummary.volume,
                price = param.tradeSummary.currentPrice,
            )
        )
    }

    override fun whenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): Boolean {
        val chart15M = param.getChart(CandleUnit.UNIT_15M)
        if (param.tradeSummary.profitRate >= 0) {
            return false
        }
        if (chart15M.getLast(1).indicators.rsi > 0.30) {
            return false
        }
        if (param.tradeSummary.profitRate >= -5.0) {
            return false
        }
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusDays(1)) {
            return false
        }
        return true
    }

    override fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        log(
            "손절",
            param.coinType,
            param.tradeSummary.volume,
            param.tradeSummary.currentPrice,
            param.tradeSummary.profitPrice
        )
        return listOf(
            SpotCoinStrategyTask.ofSellLimit(
                coinType = param.coinType,
                volume = param.tradeSummary.volume,
                price = param.tradeSummary.currentPrice,
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
    @GuideDescription("매수 주문당 거래 금액 (ex: 10만원 어치 매수)")
    val onceTradeAmount: Int,
) : StrategyConfig