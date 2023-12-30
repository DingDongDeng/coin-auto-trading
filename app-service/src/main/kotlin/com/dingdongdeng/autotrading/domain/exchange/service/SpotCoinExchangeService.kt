package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

interface SpotCoinExchangeService {
    fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyPair): SpotCoinExchangeOrderResult
    fun cancel(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrderResult
    fun getOrder(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrderResult
    fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyPair): SpotCoinExchangeChartResult
    fun support(exchangeType: ExchangeType): Boolean
}