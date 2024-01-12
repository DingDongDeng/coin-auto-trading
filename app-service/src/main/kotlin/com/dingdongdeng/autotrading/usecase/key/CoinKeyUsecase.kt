package com.dingdongdeng.autotrading.usecase.key

import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.annotation.Usecase
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

@Usecase
class CoinKeyUsecase(
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