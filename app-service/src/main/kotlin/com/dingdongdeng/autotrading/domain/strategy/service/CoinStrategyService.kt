package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.DomainService
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

@DomainService
class CoinStrategyService(
    private val strategies: List<SpotCoinStrategy>,
) {

    fun getTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>,
        strategyType: CoinStrategyType,
        autoTradeProcessorId: String,
        keyPairId: String,
        exchangeType: ExchangeType,
    ): List<SpotCoinStrategyTask> {
        val strategyService = strategies.first { it.support(strategyType) }
        return strategyService.makeTask(params, config)
    }
}