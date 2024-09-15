package com.dingdongdeng.autotrading.domain.chart.model

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.log.LoggingUtils
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import java.time.LocalDateTime

data class CachedCandles(
    private val exchangeType: ExchangeType,
    private val coinType: CoinType,
    private val unit: CandleUnit,
    private val indexMap: Map<LocalDateTime, Int>,
    private val candles: List<CoinCandle>,
) {
    val key = makeCacheKey(exchangeType, coinType, unit)
    private val dateTimes = candles.map { it.candleDateTimeKst }
    private val firstDateTime = if (candles.isEmpty()) LocalDateTime.MAX else candles.first().candleDateTimeKst
    private val lastDateTime = if (candles.isEmpty()) LocalDateTime.MIN else candles.last().candleDateTimeKst

    fun get(from: LocalDateTime, to: LocalDateTime): List<CoinCandle> {
        val startIndex = indexMap[CandleDateTimeUtils.makeUnitDateTime(from, unit, true)]
            ?: dateTimes.binarySearch(from).let { if (it < 0) -(it + 1) else it }
        val endIndex = indexMap[CandleDateTimeUtils.makeUnitDateTime(to, unit, false)]
            ?: dateTimes.binarySearch(to).let { if (it < 0) -(it + 2) else it }

        if (startIndex <= -1 || endIndex <= -1) {
            throw CriticalException.of("캐시 데이터에서 조회하려는 인덱스가 올바르지 않음, from=$from, to=$to, startIndex=$startIndex, endIndex=$endIndex, exchangeType=$exchangeType, coinType=$coinType, unit=$unit")
        }

        // from ~ to 구간에 해당하는 캔들이 없는 경우
        if (startIndex > endIndex) {
            val start = indexMap[CandleDateTimeUtils.makeUnitDateTime(from, unit, true)]
            val end = indexMap[CandleDateTimeUtils.makeUnitDateTime(to, unit, false)]
            if (start != end) {
                throw CriticalException.of("예상된 결과와 다른 시간, start=$start, end=$end")
            }
            if (start == null) {
                return emptyList()
            }
            throw CriticalException.of("캐시 데이터에서 조회하려는 인덱스가 올바르지 않음, from=$from, to=$to, startIndex=$startIndex, endIndex=$endIndex, exchangeType=$exchangeType, coinType=$coinType, unit=$unit")
        }
        if (candles[startIndex].candleDateTimeKst < from || candles[endIndex].candleDateTimeKst > to) {
            throw CriticalException.of("계산된 인덱스 결과가 올바르지 않음")
        }
        return candles.subList(startIndex, endIndex + 1)
    }

    fun hasEnough(from: LocalDateTime, to: LocalDateTime): Boolean {
        if (candles.isEmpty()) {
            return false
        }
        if (from < firstDateTime) {
            return false
        }
        if (to > lastDateTime) {
            return false
        }
        return true
    }

    // 시간 맥락을 벗어나는 조회건들은 캐시 히트를 할 수 없음 (즉, from >= firstDateTime일때 히트율이 좋을꺼라 예상됨)
    // 시간 흐름에 따라 연속적으로 데이터를 조회하는 자연스러운 상황이 아닐것이기 때문
    // 예를 들어,60분봉 2주전 누락된 캔들을 생성하려고 뜬금 없이 오래된 과거 캔들을 요청할때 등등 상황을 구분하기 위해서 사용
    fun isRightContext(from: LocalDateTime, to: LocalDateTime): Boolean {
        return from >= firstDateTime
    }

    companion object {
        fun of(
            exchangeType: ExchangeType,
            coinType: CoinType,
            unit: CandleUnit,
            candles: List<CoinCandle>,
        ): CachedCandles {
            val indexMap = candles.mapIndexed { index, coinCandle -> coinCandle.candleDateTimeKst to index }.toMap()
            return CachedCandles(
                exchangeType = exchangeType,
                coinType = coinType,
                unit = unit,
                indexMap = indexMap,
                candles = candles,
            )
        }

        fun makeCacheKey(
            exchangeType: ExchangeType,
            coinType: CoinType,
            unit: CandleUnit,
        ): String {
            val sb = StringBuilder()
            sb
                .append(LoggingUtils.getTraceId())
                .append(":")
                .append(exchangeType)
                .append(":")
                .append(coinType)
                .append(":")
                .append(unit)
            return sb.toString()
        }
    }
}