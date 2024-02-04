package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask

abstract class SpotCoinStrategySkeleton : SpotCoinStrategy {

    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        if (whenWaitTrades(params, config)) {
            return thenWaitTrades(params, config)
        }

        if (whenBuyTrade(params, config)) {
            return thenBuyTrade(params, config)
        }

        if (whenProfitTrade(params, config)) {
            return thenProfitTrade(params, config)
        }

        if (whenLossTrade(params, config)) {
            return thenLossTrade(params, config)
        }

        return emptyList()
    }

    // 미체결 주문이 존재할때
    abstract fun whenWaitTrades(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): Boolean

    // 미체결 주문이 존재할때 어떻게 할 것 인지
    abstract fun thenWaitTrades(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask>

    // 매수 주문을 해야할때
    abstract fun whenBuyTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): Boolean

    // 매수 주문을 어떻게 할 것 인지
    abstract fun thenBuyTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask>

    // 익절 주문을 해야할때
    abstract fun whenProfitTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): Boolean

    // 익절 주문을 어떻게 할 것 인지
    abstract fun thenProfitTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask>

    // 손절 주문을 해야할 때
    abstract fun whenLossTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): Boolean

    // 손절 주문을 어떻게 할 것 인지
    abstract fun thenLossTrade(
        param: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask>
}