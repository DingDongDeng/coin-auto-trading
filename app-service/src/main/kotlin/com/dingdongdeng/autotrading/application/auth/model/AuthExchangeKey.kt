package com.dingdongdeng.autotrading.application.auth.model

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

data class AuthExchangeKey(
    val exchangeType: ExchangeType,
    val keyPairId: String,
) {
    companion object {
        fun of(keyPair: ExchangeKeyPair) = AuthExchangeKey(keyPair.exchangeType, keyPair.keyPairId)
    }
}
