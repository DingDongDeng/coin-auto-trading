package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask

abstract class SpotCoinStrategySkeleton<T> : SpotCoinStrategy {

    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        val convertedConfig = convertConfig(config)
        if (whenWaitTrades(params, convertedConfig)) {
            return thenWaitTrades(params, convertedConfig)
        }

        if (whenBuyTrade(params, convertedConfig)) {
            return thenBuyTrade(params, convertedConfig)
        }

        if (whenProfitTrade(params, convertedConfig)) {
            return thenProfitTrade(params, convertedConfig)
        }

        if (whenLossTrade(params, convertedConfig)) {
            return thenLossTrade(params, convertedConfig)
        }

        return emptyList()
    }

    abstract fun convertConfig(
        config: Map<String, Any>
    ): T

    // 미체결 주문이 존재할때
    abstract fun whenWaitTrades(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): Boolean

    // 미체결 주문이 존재할때 어떻게 할 것 인지
    abstract fun thenWaitTrades(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): List<SpotCoinStrategyTask>

    // 매수 주문을 해야할때
    abstract fun whenBuyTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): Boolean

    // 매수 주문을 어떻게 할 것 인지
    abstract fun thenBuyTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): List<SpotCoinStrategyTask>

    // 익절 주문을 해야할때
    abstract fun whenProfitTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): Boolean

    // 익절 주문을 어떻게 할 것 인지
    abstract fun thenProfitTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): List<SpotCoinStrategyTask>

    // 손절 주문을 해야할 때
    abstract fun whenLossTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): Boolean

    // 손절 주문을 어떻게 할 것 인지
    abstract fun thenLossTrade(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: T
    ): List<SpotCoinStrategyTask>
}