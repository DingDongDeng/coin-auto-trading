package com.dingdongdeng.autotrading.domain.chart.repository

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
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
    private val cachedData = mutableMapOf<String, List<CoinCandle>>()

    fun findAllCoinCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle> {
        val key = makeCacheKey(exchangeType, coinType, unit)
        synchronized(this) {
            if (isEnoughCacheData(key, from, to).not()) {
                saveCachedData(key, exchangeType, coinType, unit, from, to)
            }
            return getCachedData(key, from, to)
        }
    }

    private fun makeCacheKey(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
    ): String {
        val sb = StringBuilder()
        sb.append(exchangeType)
            .append(":")
            .append(coinType)
            .append(":")
            .append(unit)
        return sb.toString()
    }

    private fun isEnoughCacheData(
        key: String,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Boolean {
        val cachedCandles = cachedData[key] ?: emptyList()
        if (cachedCandles.isEmpty()) {
            return false
        }
        val firstDateTime = cachedCandles.first().candleDateTimeKst
        if (from < firstDateTime) {
            return false
        }
        val lastDateTime = cachedCandles.last().candleDateTimeKst
        if (to > lastDateTime) {
            return false
        }
        return true
    }

    private fun getCachedData(
        key: String,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle> {
        val candles = cachedData[key]

        if (candles.isNullOrEmpty()) {
            throw CriticalException.of("캐싱된 데이터가 존재하지 않습니다. key=$key, from=$from, to=$to")
        }

        val startIndex = candles.indexOfFirst { from <= it.candleDateTimeKst }
        val endIndex = candles.indexOfLast { to >= it.candleDateTimeKst }

        if (startIndex == -1 || endIndex == -1) {
            throw CriticalException.of("캐싱된 데이터에서 대상을 찾지 못했습니다. key=$key, from=$from, to=$to")
        }

        return candles.subList(startIndex, endIndex + 1)
    }

    private fun saveCachedData(
        key: String,
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ) {
        val candles = coinCandleRepository.findAllCoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = unit,
            from = from.minusSeconds(unit.getSecondSize() * (CACHED_CANDLE_COUNT * 1 / 4)), // 25%
            to = to.plusSeconds(unit.getSecondSize() * (CACHED_CANDLE_COUNT * 3 / 4)), // 75%
        )
        cachedData[key] = candles
    }

    companion object {
        const val CACHED_CANDLE_COUNT = 200 // 테스트 결과 200개가 가장 효율적
    }
}