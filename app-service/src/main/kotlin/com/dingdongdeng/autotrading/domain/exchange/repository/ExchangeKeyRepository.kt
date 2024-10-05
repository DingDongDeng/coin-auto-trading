package com.dingdongdeng.autotrading.domain.exchange.repository

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeKey
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangeKeyRepository : JpaRepository<ExchangeKey, Long> {

    fun findByKeyPairId(keyPairId: String): List<ExchangeKey>

    fun findAllByExchangeTypeAndUserId(exchangeType: ExchangeType, userId: Long): List<ExchangeKey>
}