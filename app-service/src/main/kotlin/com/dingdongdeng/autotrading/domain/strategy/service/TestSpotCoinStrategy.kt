package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import org.springframework.stereotype.Component

@Component
class TestSpotCoinStrategy : SpotCoinStrategy {
    override fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): List<SpotCoinStrategyTask> {
        // TODO
        return emptyList()
    }

    override fun support(param: CoinStrategyType): Boolean {
        // TODO
        return true
    }
}