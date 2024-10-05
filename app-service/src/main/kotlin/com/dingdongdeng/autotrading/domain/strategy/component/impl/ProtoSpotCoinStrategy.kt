package com.dingdongdeng.autotrading.domain.strategy.component.impl

import com.dingdongdeng.autotrading.domain.strategy.component.SimpleSpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.component.annotation.GuideDescription
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ProtoSpotCoinStrategy(
    private val objectMapper: ObjectMapper,
) : SimpleSpotCoinStrategy<ExampleSpotCoinStrategyConfig>() {

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
        return param.tradeSummary.getOldWaitTrades(60L)
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

        // 12시간 숨 고르기
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusHours(12)) {
            return false
        }

        // 하락 추세라면 (약 36시간 추세 분석)
        val candles = param.getChart(CandleUnit.UNIT_15M).candles.takeLast(145)
        val acc = candles.sumOf { it.closingPrice - it.indicators.ma.sma120 }
        if (acc < 0) {
            return false
        }
        return false
    }

    override fun thenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return listOf(
            SpotCoinStrategyTask.ofLimitBuy(
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
        if (param.tradeSummary.profitRate <= 0) {
            return false
        }
        // 매수 시점으로부터 36시간 동안 관망
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusHours(36)) {
            return false
        }
        // 상승 추세라면 (약 36시간 추세 분석)
        val candles = param.getChart(CandleUnit.UNIT_15M).candles.takeLast(145)
        val acc = candles.sumOf { it.closingPrice - it.indicators.ma.sma120 }
        if (acc > 0) {
            return false
        }
        return true
    }

    override fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return listOf(
            SpotCoinStrategyTask.ofLimitSell(
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
        if (param.tradeSummary.profitRate >= 0) {
            return false
        }
        // 매수 시점으로부터 36시간 동안 관망
        if (param.tradeSummary.lastTradedAt > TimeContext.now().minusHours(36)) {
            return false
        }
        // 하락 추세라면 (약 36시간 추세 분석)
        val candles = param.getChart(CandleUnit.UNIT_15M).candles.takeLast(145)
        val acc = candles.sumOf { it.closingPrice - it.indicators.ma.sma120 }
        if (acc < 0) {
            return false
        }
        return true
    }

    override fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: ExampleSpotCoinStrategyConfig
    ): List<SpotCoinStrategyTask> {
        return listOf(
            SpotCoinStrategyTask.ofLimitSell(
                coinType = param.coinType,
                volume = param.tradeSummary.volume,
                price = param.tradeSummary.currentPrice,
            )
        )
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.PROTO
    }
}

data class ProtoSpotCoinStrategyConfig(
    @GuideDescription("매수 주문당 거래 금액 (ex: 10만원 어치 매수)")
    val onceTradeAmount: Int,
) : StrategyConfig