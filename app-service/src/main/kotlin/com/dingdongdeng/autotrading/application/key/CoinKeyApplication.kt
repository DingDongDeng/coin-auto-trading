package com.dingdongdeng.autotrading.application.key

import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.annotation.Application
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

@Application
class CoinKeyApplication(
    private val coinExchangeServices: List<SpotCoinExchangeService>
) {
    fun registerKey(
        exchangeType: ExchangeType,
        accessKey: String,
        secretKey: String,
        userId: Long
    ): String {
        val exchangeService = coinExchangeServices.first { it.support(exchangeType) }
        return exchangeService.registerKeyPair(
            accessKey = accessKey,
            secretKey = secretKey,
            userId = userId,
        )
    }
}