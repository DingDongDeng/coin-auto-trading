package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskResult
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType

class TestSpotCoinStrategy : SpotCoinStrategy {
    override fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): List<SpotCoinStrategyMakeTaskResult> {
        TODO("Not yet implemented")
    }

    override fun handleResult() {
        TODO("Not yet implemented")
    }

    override fun support(param: CoinStrategyType): Boolean {
        TODO("Not yet implemented")
    }
}