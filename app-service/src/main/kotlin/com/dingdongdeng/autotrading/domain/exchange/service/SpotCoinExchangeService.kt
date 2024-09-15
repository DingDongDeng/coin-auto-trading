package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByCountParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByDateTimeParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

interface SpotCoinExchangeService {
    fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder
    fun cancel(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder
    fun getOrder(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder

    // from <= 범위 <= to
    fun getChartByDateTime(param: SpotCoinExchangeChartByDateTimeParam, keyParam: ExchangeKeyPair): ExchangeChart
    fun getChartByCount(param: SpotCoinExchangeChartByCountParam, keyParam: ExchangeKeyPair): ExchangeChart
    fun getKeyPair(keyPairId: String): ExchangeKeyPair
    fun getKeyPairs(userId: Long): List<ExchangeKeyPair>
    fun registerKeyPair(accessKey: String, secretKey: String, userId: Long): String
    fun removeKeyPair(keyPairId: String): String
    fun support(exchangeType: ExchangeType, mode: ExchangeModeType): Boolean
}