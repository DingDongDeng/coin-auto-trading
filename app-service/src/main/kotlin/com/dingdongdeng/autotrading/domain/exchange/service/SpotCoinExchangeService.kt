package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

interface SpotCoinExchangeService {
    fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder
    fun cancel(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder
    fun getOrder(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder
    fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyPair): ExchangeChart
    fun getKeyPair(keyPairId: String): ExchangeKeyPair
    fun registerKeyPair(exchangeType: ExchangeType, keyName: String, keyValue: String, userId: Long)
    fun support(exchangeType: ExchangeType): Boolean
}