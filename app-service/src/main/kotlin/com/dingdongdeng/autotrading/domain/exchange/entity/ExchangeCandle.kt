package com.dingdongdeng.autotrading.domain.exchange.entity

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "exchange_candle",
    indexes = [
        Index(
            name = "idx_exchange_candle_1",
            columnList = "exchange_type,coin_type,candle_unit,candle_date_time_kst",
            unique = true
        ),
    ]
)
class ExchangeCandle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_candle_id")
    val id: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_type")
    val exchangeType: ExchangeType,
    @Enumerated(EnumType.STRING)
    @Column(name = "coin_type")
    val coinType: CoinType,
    @Enumerated(EnumType.STRING)
    @Column(name = "candle_unit")
    val unit: CandleUnit,
    @Column(name = "candle_date_time_utc")
    val candleDateTimeUtc: LocalDateTime,
    @Column(name = "candle_date_time_kst")
    val candleDateTimeKst: LocalDateTime,
    @Column(name = "opening_price")
    val openingPrice: Double,
    @Column(name = "high_price")
    val highPrice: Double,
    @Column(name = "low_price")
    val lowPrice: Double,
    @Column(name = "closing_price")
    val closingPrice: Double,
    @Column(name = "acc_trade_price")
    val accTradePrice: Double,
    @Column(name = "acc_trade_volume")
    val accTradeVolume: Double,
)
