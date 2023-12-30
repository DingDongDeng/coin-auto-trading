package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskResult
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType

interface SpotCoinStrategy {

    fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): List<SpotCoinStrategyMakeTaskResult>

    fun handleResult()

    fun support(param: CoinStrategyType): Boolean
}