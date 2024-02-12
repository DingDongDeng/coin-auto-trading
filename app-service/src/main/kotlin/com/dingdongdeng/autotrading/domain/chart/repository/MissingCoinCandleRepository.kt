package com.dingdongdeng.autotrading.domain.chart.repository

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.entity.MissingCoinCandle
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface MissingCoinCandleRepository : JpaRepository<MissingCoinCandle, Long> {

    @Query(
        """
        select candle
        from MissingCoinCandle candle
        where candle.exchangeType = :exchangeType
        and candle.coinType = :coinType
        and candle.unit = :unit
        and candle.candleDateTimeKst between :from and :to
        order by candle.candleDateTimeKst
    """
    )
    fun findAllMissingCoinCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle>
}