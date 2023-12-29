package com.dingdongdeng.autotrading.domain.exchange.entity

import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "exchange_key")
class ExchangeKey(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_key_id")
    val id: Long? = null,
    @Column(name = "pair_id")
    val pairId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "coin_exchange_type")
    val coinExchangeType: CoinExchangeType,
    @Column(name = "name")
    val name: String,
    @Column(name = "`value`")
    val value: String,
    @Column(name = "user_id")
    val userId: String,
)
