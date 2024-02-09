package com.dingdongdeng.autotrading.domain.chart.repository

import com.dingdongdeng.autotrading.domain.chart.entity.ExchangeCandle
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ExchangeCandleRepository : JpaRepository<ExchangeCandle, Long> {

    @Query(
        """
        select candle
        from ExchangeCandle candle
        where candle.exchangeType = :exchangeType
        and candle.coinType = :coinType
        and candle.unit = :unit
        and candle.candleDateTimeKst between :from and :to
        order by candle.candleDateTimeKst
    """
    )
    fun findAllExchangeCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<ExchangeCandle>

    @Query(
        """
        select count(candle)
        from ExchangeCandle candle
        where candle.exchangeType = :exchangeType
        and candle.coinType = :coinType
        and candle.unit = :unit
        and candle.candleDateTimeKst between :from and :to
    """
    )
    fun countByExchangeCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Long
}