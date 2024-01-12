package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType

interface SpotCoinStrategy {

    fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>, config: Map<String, Any>): List<SpotCoinStrategyTask>

    fun support(param: CoinStrategyType): Boolean
}