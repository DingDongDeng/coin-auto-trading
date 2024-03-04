package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.DomainService

@DomainService
class CoinStrategyService(
    private val strategies: List<SpotCoinStrategy>,
) {

    fun getTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>,
        strategyType: CoinStrategyType,
    ): List<SpotCoinStrategyTask> {
        val strategyService = strategies.first { it.support(strategyType) }
        return strategyService.makeTask(params, config)
    }
}