package com.dingdongdeng.autotrading.domain.trade.entity

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderState
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "coin_trade_history")
class CoinTradeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coin_trade_history_id")
    val id: Long? = null,
    @Column(name = "order_id")
    val orderId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    val state: OrderState,
    @Column(name = "auto_trade_processor_id")
    val autoTradeProcessorId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "coin_exchange_type")
    val exchangeType: ExchangeType,
    @Enumerated(EnumType.STRING)
    @Column(name = "coint_type")
    val coinType: CoinType,
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    val orderType: OrderType,
    @Enumerated(EnumType.STRING)
    @Column(name = "price_type")
    val priceType: PriceType,
    @Column(name = "volume")
    val volume: Double,
    @Column(name = "price")
    val price: Double,
    @Column(name = "fee")
    val fee: Double,
    @Column(name = "traded_at")
    val tradedAt: LocalDateTime,
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
