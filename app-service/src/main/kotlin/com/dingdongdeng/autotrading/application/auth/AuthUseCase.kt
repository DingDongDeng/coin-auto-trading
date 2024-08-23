package com.dingdongdeng.autotrading.application.auth

import com.dingdongdeng.autotrading.application.auth.model.AuthExchangeKey
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

@UseCase
class AuthUseCase(
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

    fun getKeys(userId: Long): List<AuthExchangeKey> {
        return coinExchangeServices
            .filter { service -> ExchangeType.ofNotBackTest().any { type -> service.support(type) } }
            .flatMap { it.getKeyPairs(userId) }
            .map { AuthExchangeKey.of(it) }
    }
}