package com.dingdongdeng.autotrading.domain.chart.repository

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.model.CachedCandles
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CachedCoinCandleRepository(
    private val coinCandleRepository: CoinCandleRepository,
) {
    // traceId 단위로 캐싱 데이터 관리
    private val cachedData = mutableMapOf<String, CachedCandles>()

    fun findAllCoinCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle> {
        if (from > to) {
            throw CriticalException.of("조회 범위가 적절하지 않음, from=$from, to=$to")
        }
        val key = CachedCandles.makeCacheKey(exchangeType, coinType, unit)
        synchronized(cachedData[key] ?: cachedData) {
            val cachedCandles = cachedData[key] ?: saveCachedData(exchangeType, coinType, unit, from, to)
            if (cachedCandles.hasEnough(from, to)) {
                return cachedCandles.get(from, to)
            }

            if (cachedCandles.isRightContext(from, to)) {
                return findData(exchangeType, coinType, unit, from, to)
            }
            return saveCachedData(exchangeType, coinType, unit, from, to).get(from, to)
        }
    }

    private fun saveCachedData(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): CachedCandles {
        val candles = findData(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = unit,
            from = from.minusSeconds(unit.getSecondSize() * (CACHED_CANDLE_COUNT * PREV_BUFFER_RATE / 100)),
            to = to.plusSeconds(unit.getSecondSize() * (CACHED_CANDLE_COUNT * NEXT_BUFFER_RATE / 100)),
        )
        val cachedCandles = CachedCandles.of(exchangeType, coinType, unit, candles)

        cachedData[cachedCandles.key] = cachedCandles
        return cachedCandles
    }

    private fun findData(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle> {
        return coinCandleRepository.findAllCoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = unit,
            from = from,
            to = to,
        )
    }

    companion object {
        private const val PREV_BUFFER_RATE = 10
        private const val NEXT_BUFFER_RATE = 90
        private const val CACHED_CANDLE_COUNT = 1000
    }
}