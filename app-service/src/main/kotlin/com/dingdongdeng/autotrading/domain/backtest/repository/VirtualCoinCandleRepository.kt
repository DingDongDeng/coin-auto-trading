package com.dingdongdeng.autotrading.domain.backtest.repository

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.repository.CachedCoinCandleRepository
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.infra.common.utils.toUtc
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

@Repository
class VirtualCoinCandleRepository(
    private val cachedCoinCandleRepository: CachedCoinCandleRepository,
) {
    fun findAllCoinCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CoinCandle> {
        // 캔들 조회
        val dbCandles = cachedCoinCandleRepository.findAllCoinCandle(exchangeType, coinType, unit, from, to)

        // 가상 캔들 생성 (최소 단위 캔들을 사용하여, 현재 시간 기준으로 생성)
        // 예를 들어, 15분봉일때, 현재시간이 13:17이라면 13:15에 대한 가상캔들을 새로 만들어야한다.
        // DB에서 조회한 13:15캔들은 13:30까지의 상태가 반영되었기 때문이다.
        // 만약, 이 시간에 거래가 없어서 캔들이 존재하지 않는다면 null을 반환한다.
        val lastCandle: CoinCandle? = makeVirtualCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = unit,
            from = CandleDateTimeUtils.makeUnitDateTime(to, unit),
            to = to,
        )

        val candles = mutableListOf<CoinCandle>().apply {
            addAll(dbCandles)
            sortBy { it.candleDateTimeKst }
            if (lastCandle != null) {
                if (isNotEmpty() && lastCandle.candleDateTimeKst == last().candleDateTimeKst) {
                    removeAt(size - 1)
                }
                add(lastCandle)
            }
        }

        return candles
    }

    private fun makeVirtualCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        unit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): CoinCandle? {

        // 큰 단위의 캔들을 다시 만들기 위해 가장 작은 단위의 캔들을 조회
        val minUnitCandles = cachedCoinCandleRepository.findAllCoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = CandleUnit.min(),
            from = from,
            to = to,
        )

        if (minUnitCandles.isEmpty()) {
            log.warn("백테스트를 위한 minUnitCandles 조회에 실패, exchangeType=$exchangeType, coinType=$coinType, unitType=$unit from=$from, to=$to")
            return null
        }

        val openingPrice = minUnitCandles.first().openingPrice
        var highPrice = 0.0
        var lowPrice = 0.0
        var closingPrice = 0.0
        var accTradePrice = 0.0
        var accTradeVolume = 0.0

        minUnitCandles.forEach { candle ->
            highPrice = max(openingPrice, candle.closingPrice)
            lowPrice = min(openingPrice, candle.closingPrice)
            closingPrice = candle.closingPrice
            accTradePrice += candle.accTradePrice
            accTradeVolume += candle.accTradeVolume
        }

        return CoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = unit,
            candleDateTimeUtc = from.toUtc(),
            candleDateTimeKst = from,
            openingPrice = openingPrice,
            highPrice = highPrice,
            lowPrice = lowPrice,
            closingPrice = closingPrice,
            accTradePrice = accTradePrice,
            accTradeVolume = accTradeVolume,
        )
    }
}