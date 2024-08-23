package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

data class ExchangeKeyPair(
    val exchangeType: ExchangeType,
    val keyPairId: String,
    val accessKey: String,
    val secretKey: String,
)

