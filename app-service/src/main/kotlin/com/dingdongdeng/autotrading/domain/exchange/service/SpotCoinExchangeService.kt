package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType

interface SpotCoinExchangeService {
    fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult
    fun cancel(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult
    fun getOrder(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult
    fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyParam): SpotCoinExchangeChartResult
    fun support(exchangeType: CoinExchangeType): Boolean
}