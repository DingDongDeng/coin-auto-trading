package com.dingdongdeng.autotrading.domain.strategy.service

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskResult

interface SpotCoinStrategy {

    fun makeTask(params: List<SpotCoinStrategyMakeTaskParam>): SpotCoinStrategyMakeTaskResult

    fun handleTaskResult()

    fun getStrategyType()
}