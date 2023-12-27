package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.type.CoinExchangeType

interface SpotCoinExchangeService {
    fun support(exchangeType: CoinExchangeType): Boolean
    fun order(param: SpotCoinExchangeOrderParam): SpotCoinExchangeOrderResult
    fun cancel(orderId: String): SpotCoinExchangeOrderResult
    fun getOrder(orderId: String): SpotCoinExchangeOrderResult
    fun getChart(param: SpotCoinExchangeChartParam): SpotCoinExchangeChartResult
}