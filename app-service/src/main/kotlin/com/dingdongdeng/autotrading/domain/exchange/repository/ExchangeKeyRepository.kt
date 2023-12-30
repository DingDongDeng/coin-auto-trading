package com.dingdongdeng.autotrading.domain.exchange.repository

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeKey
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangeKeyRepository : JpaRepository<ExchangeKey, Long> {

    fun findByKeyPairId(keyPairId: String): List<ExchangeKey>
}