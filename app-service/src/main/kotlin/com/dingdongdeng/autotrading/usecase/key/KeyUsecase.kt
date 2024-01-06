package com.dingdongdeng.autotrading.usecase.key

import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Service

@Service
class KeyUsecase(
    private val exchangeServices: List<SpotCoinExchangeService>
) {
    fun registerCoinExchangeKey(
        exchangeType: ExchangeType,
        accessKey: String,
        secretKey: String,
        userId: Long
    ): String {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        return exchangeService.registerKeyPair(
            accessKey = accessKey,
            secretKey = secretKey,
            userId = userId,
        )
    }
}