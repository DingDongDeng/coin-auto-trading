package com.dingdongdeng.autotrading.domain.backtest.service

import com.dingdongdeng.autotrading.domain.backtest.service.impl.BackTestSpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.repository.CachedCoinCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

@Component
class VirtualCoinChartService(
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
        val candles = cachedCoinCandleRepository.findAllCoinCandle(
            exchangeType = exchangeType,
            coinType = param.coinType,
            unit = param.candleUnit,
            from = param.from,
            to = param.to,
        )

        // 가상 캔들 생성 (최소 단위 캔들을 사용하여, 현재 시간 기준으로 생성)
        // 예를 들어, 15분봉일때, 현재시간이 13:17이라면 13:15에 대한 가상캔들을 새로 만들어야한다.
        // DB에서 조회한 13:15캔들은 13:30까지의 상태가 반영되었기 때문이다.
        val virtualCandle = makeVirtualCandle(
            coinType = param.coinType,
            candleUnit = param.candleUnit,
            from = CandleDateTimeUtils.makeUnitDateTime(param.to, param.candleUnit),
            to = param.to, // 백테스트에서는 now 사용됨
        )

        // 최종 캔들
        val resultCandles = candles
            .subList(0, candles.size - 1) // 마지막 캔들을 가상 캔들로 대체
            .map {
                ExchangeChartCandle(
                    candleUnit = it.unit,
                    candleDateTimeUtc = it.candleDateTimeUtc,
                    candleDateTimeKst = it.candleDateTimeKst,
                    openingPrice = it.openingPrice,
                    highPrice = it.highPrice,
                    lowPrice = it.lowPrice,
                    closingPrice = it.closingPrice,
                    accTradePrice = it.accTradePrice,
                    accTradeVolume = it.accTradeVolume,
                )
            } + virtualCandle
    }

    private fun makeVirtualCandle(
        coinType: CoinType,
        candleUnit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime
    ): ExchangeChartCandle {

        // 큰 단위의 캔들을 다시 만들기 위해 가장 작은 단위의 캔들을 조회
        val minUnitCandles = cachedCoinCandleRepository.findAllCoinCandle(
            exchangeType = BackTestSpotCoinExchangeService.EXCHANGE_TYPE_FOR_BACKTEST,
            coinType = coinType,
            unit = CandleUnit.min(),
            from = from,
            to = to,
        ).takeIf { it.isNotEmpty() } // 거래소 캔들이 누락되어 있음 (캔들이 누락된 경우가 빈번함)
            ?: cachedCoinCandleRepository.findAllCoinCandle(
                exchangeType = BackTestSpotCoinExchangeService.EXCHANGE_TYPE_FOR_BACKTEST,
                coinType = coinType,
                unit = CandleUnit.min(),
                from = from.minusHours(10), // 과거 캔들로 메꾼다다. (미래 캔들로 하면 선행 지표처럼 작용할수도 있으니 주의)
                to = to,
            ).takeLast(1)

        if (minUnitCandles.isEmpty()) {
            throw CriticalException.of("백테스트를 위한 minCandles 조회에 실패하였습니다. coinType=$coinType, unitType=$candleUnit from=$from, to=$to")
        }

        val candleDateTimeUtc = minUnitCandles.first().candleDateTimeUtc
        val candleDateTimeKst = minUnitCandles.first().candleDateTimeKst
        val openingPrice = minUnitCandles.first().openingPrice
        var highPrice = 0.0
        var lowPrice = 0.0
        var closingPrice = 0.0
        var accTradePrice = 0.0
        var accTradeVolume = 0.0

        minUnitCandles.forEach { candle ->
            highPrice = max(highPrice, candle.closingPrice)
            lowPrice = min(lowPrice, candle.closingPrice)
            closingPrice = candle.closingPrice
            accTradePrice += candle.accTradePrice
            accTradeVolume += candle.accTradeVolume
        }

        return ExchangeChartCandle(
            candleUnit = candleUnit,
            candleDateTimeUtc = candleDateTimeUtc,
            candleDateTimeKst = candleDateTimeKst,
            openingPrice = openingPrice,
            highPrice = highPrice,
            lowPrice = lowPrice,
            closingPrice = closingPrice,
            accTradePrice = accTradePrice,
            accTradeVolume = accTradeVolume,
        )
    }
}