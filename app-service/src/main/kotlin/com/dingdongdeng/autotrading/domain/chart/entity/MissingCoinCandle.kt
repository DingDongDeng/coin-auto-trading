package com.dingdongdeng.autotrading.domain.chart.entity

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
    name = "missing_coin_candle",
    indexes = [
        Index(
            name = "idx_missing_coin_candle_1",
            columnList = "exchange_type,coin_type,candle_unit,candle_date_time_kst",
            unique = true
        ),
    ]
)
class MissingCoinCandle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "missing_coin_candle_id")
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
)
