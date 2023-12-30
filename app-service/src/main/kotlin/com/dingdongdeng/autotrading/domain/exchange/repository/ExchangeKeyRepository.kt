package com.dingdongdeng.autotrading.domain.exchange.repository

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeKey
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangeKeyRepository : JpaRepository<ExchangeKey, Long> {

    fun findByExchangeTypeAndKeyPairId(exchangeType: ExchangeType, keyPairId: String): List<ExchangeKey>
}