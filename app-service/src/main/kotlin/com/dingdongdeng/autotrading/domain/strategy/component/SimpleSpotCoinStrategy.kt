package com.dingdongdeng.autotrading.domain.strategy.component

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask

abstract class SimpleSpotCoinStrategy<T> : SpotCoinStrategy {

    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        val convertedConfig = convertConfig(config)
        return params.flatMap { param ->
            if (whenWaitTrades(param, convertedConfig)) {
                return@flatMap thenWaitTrades(param, convertedConfig)
            }

            if (whenBuyTrade(param, convertedConfig)) {
                return@flatMap thenBuyTrade(param, convertedConfig)
            }

            if (param.tradeSummary.hasVolume) {
                if (whenProfitTrade(param, convertedConfig)) {
                    return@flatMap thenProfitTrade(param, convertedConfig)
                }

                if (whenLossTrade(param, convertedConfig)) {
                    return@flatMap thenLossTrade(param, convertedConfig)
                }
            }
            return@flatMap emptyList()
        }
    }

    abstract fun convertConfig(
        config: Map<String, Any>
    ): T

    // 미체결 주문이 존재할때
    abstract fun whenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): Boolean

    // 미체결 주문이 존재할때 어떻게 할 것 인지
    abstract fun thenWaitTrades(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): List<SpotCoinStrategyTask>

    // 매수 주문을 해야할때
    abstract fun whenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): Boolean

    // 매수 주문을 어떻게 할 것 인지
    abstract fun thenBuyTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): List<SpotCoinStrategyTask>

    // 익절 주문을 해야할때
    abstract fun whenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): Boolean

    // 익절 주문을 어떻게 할 것 인지
    abstract fun thenProfitTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): List<SpotCoinStrategyTask>

    // 손절 주문을 해야할 때
    abstract fun whenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): Boolean

    // 손절 주문을 어떻게 할 것 인지
    abstract fun thenLossTrade(
        param: SpotCoinStrategyMakeTaskParam,
        config: T
    ): List<SpotCoinStrategyTask>
}