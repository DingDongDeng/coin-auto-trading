package com.dingdongdeng.autotrading.domain.exchange

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType

interface SpotCoinExchangeService {
    fun order(param: SpotCoinExchangeOrderParam): SpotCoinExchangeOrderResult
    fun cancel(orderId: String): SpotCoinExchangeOrderResult
    fun getOrder(orderId: String): SpotCoinExchangeOrderResult
    fun getChart(param: SpotCoinExchangeChartParam): SpotCoinExchangeChartResult
    fun support(exchangeType: CoinExchangeType): Boolean
}