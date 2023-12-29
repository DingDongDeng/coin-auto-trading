package com.dingdongdeng.autotrading.domain.strategy.service

interface SpotCoinStrategy {

    fun execute()

    fun handleResult()

    fun getStrategyType()
}