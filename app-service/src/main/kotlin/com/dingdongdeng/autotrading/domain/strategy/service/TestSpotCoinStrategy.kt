package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskResult

class TestSpotCoinStrategy : SpotCoinStrategy {
    override fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): List<SpotCoinStrategyMakeTaskResult> {
        TODO("Not yet implemented")
    }

    override fun handleTaskResult() {
        TODO("Not yet implemented")
    }

    override fun getStrategyType() {
        TODO("Not yet implemented")
    }
}