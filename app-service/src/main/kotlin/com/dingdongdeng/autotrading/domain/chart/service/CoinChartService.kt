package com.dingdongdeng.autotrading.domain.chart.service

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.entity.MissingCoinCandle
import com.dingdongdeng.autotrading.domain.chart.model.Candle
import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.chart.repository.CoinCandleRepository
import com.dingdongdeng.autotrading.domain.chart.repository.MissingCoinCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.factory.IndicatorFactory
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.minDate
import com.dingdongdeng.autotrading.infra.common.utils.toUtc
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinChartService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinCandleRepository: CoinCandleRepository,
    private val missingCoinCandleRepository: MissingCoinCandleRepository,
) {

    fun getCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<Chart> {
        return AsyncUtils.joinAll(candleUnits) { candleUnit ->
            makeChartProcess(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                coinType = coinType,
                candleUnit = candleUnit,
                from = from,
                to = to,
            )
        }
    }

    fun getMissingChart(
        exchangeType: ExchangeType,
        coinType: CoinType,
        candleUnit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Chart {
        val missingCandles =
            missingCoinCandleRepository.findAllMissingCoinCandle(exchangeType, coinType, candleUnit, from, to)
        return Chart(
            from = from,
            to = to,
            currentPrice = 0.0,
            candleUnit = candleUnit,
            candles = missingCandles.map { missingCandle ->
                Candle(
                    candleUnit = candleUnit,
                    candleDateTimeUtc = missingCandle.candleDateTimeUtc,
                    candleDateTimeKst = missingCandle.candleDateTimeKst,
                    openingPrice = 0.0,
                    highPrice = 0.0,
                    lowPrice = 0.0,
                    closingPrice = 0.0,
                    accTradePrice = 0.0,
                    accTradeVolume = 0.0,
                    indicatorsFunc = { throw WarnException.of("missingCandle은 보조지표를 지원하지 않음") },
                )
            },
        )
    }


    fun loadCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
    ) {
        if (endDateTime > TimeContext.now()) {
            throw WarnException.of("현재 시점보다 미래의 캔들을 다운로드 할 수 없습니다.")
        }
        if (candleUnits.contains(CandleUnit.min()).not()) {
            throw WarnException.of("거래소의 캔들을 서버에 다운로드 할때는 가장 작은 캔들 단위를 포함해야만 합니다. min=${CandleUnit.min()}")
        }
        AsyncUtils.joinAll(candleUnits) { candleUnit ->
            loadChartProcess(
                exchangeType = exchangeType,
                coinType = coinType,
                candleUnit = candleUnit,
                from = startDateTime,
                to = endDateTime,
                keyPairId = keyPairId,
            )
        }
    }

    private fun makeChartProcess(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Chart {
        val chartParam = SpotCoinExchangeChartParam(
            coinType = coinType,
            candleUnit = candleUnit,
            from = from.minusSeconds(candleUnit.getSecondSize() * (CALCULATE_INDICATOR_CANDLE_COUNT + CANDLE_COUNT_BUFFER)),
            to = to,
        )
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        val exchangeChart = exchangeService.getChart(chartParam, exchangeKeyPair)
        val exchangeCandles = exchangeChart.candles.sortedBy { it.candleDateTimeKst } // 혹시 모르니 한번 더 정렬

        val candles = mutableListOf<Candle>()
        for (startIndex in exchangeCandles.indices) {
            val endIndex = startIndex + CALCULATE_INDICATOR_CANDLE_COUNT - 1

            val subCandles = exchangeCandles.subList(startIndex, endIndex + 1) // 참조만 변경 (deep copy x)

            if (subCandles.size < CALCULATE_INDICATOR_CANDLE_COUNT) {
                throw CriticalException.of("생성된 subCandles가 적절한 개수가 아님")
            }
            val candle = subCandles.last()
            candles.add(
                Candle(
                    candleUnit = candle.candleUnit,
                    candleDateTimeUtc = candle.candleDateTimeUtc,
                    candleDateTimeKst = candle.candleDateTimeKst,
                    openingPrice = candle.openingPrice,
                    highPrice = candle.highPrice,
                    lowPrice = candle.lowPrice,
                    closingPrice = candle.closingPrice,
                    accTradePrice = candle.accTradePrice,
                    accTradeVolume = candle.accTradeVolume,
                    indicatorsFunc = { IndicatorFactory.calculate(subCandles) }
                )
            )
        }

        if (candles.size < CALCULATE_INDICATOR_CANDLE_COUNT) {
            throw CriticalException.of("캔들의 보조 지표 계산을 위한 적절한 수의 과거 캔들을 추출하는데 실패")
        }

        if (candles.last().candleDateTimeKst != CandleDateTimeUtils.makeUnitDateTime(to, candleUnit)) {
            throw CriticalException.of("생성된 캔들의 마지막 요소 시간이 단위시간과 상이함, to=$to, unit=$candleUnit, candleLastDateTime=${candles.last().candleDateTimeKst}")
        }

        if (candles.first().candleDateTimeKst != CandleDateTimeUtils.makeUnitDateTime(from, candleUnit, true)) {
            throw CriticalException.of("생성된 캔들의 첫번째 요소 시간이 단위시간과 상이함, from=$from, unit=$candleUnit, candleLastDateTime=${candles.first().candleDateTimeKst}")
        }

        return Chart(
            from = candles.first().candleDateTimeKst,
            to = candles.last().candleDateTimeKst,
            currentPrice = candles.last().closingPrice,
            candleUnit = candleUnit,
            candles = candles,
        )
    }

    private fun loadChartProcess(
        exchangeType: ExchangeType,
        coinType: CoinType,
        candleUnit: CandleUnit,
        from: LocalDateTime,
        to: LocalDateTime,
        keyPairId: String,
    ) {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val keyParam = exchangeService.getKeyPair(keyPairId)

        /*
         *  아래 과정을 구간별 반복
         *  1. 거래소에서 캔들을 조회한다.
         *  2. 거래소에서 누락된 캔들을 특정한다.
         *  3. 거래소에서 누락된 캔들 정보를 저장한다.
         *  4. DB에 저장되지 않는 캔들을 특정한다.
         *  5. DB에 없는 캔들을 저장한다.
         */
        var startDateTime = from
        while (true) {
            if (startDateTime >= to) {
                break
            }
            val endDateTime = minDate(startDateTime.plusSeconds(candleUnit.getSecondSize() * CHART_LOAD_CHUNK_SIZE), to)

            // 거래소에서 조회한 캔들
            val exchangeCandles = exchangeService.getChart(
                SpotCoinExchangeChartParam(coinType, candleUnit, startDateTime, endDateTime),
                keyParam
            ).candles

            loadMissingCandle(
                exchangeType = exchangeType,
                coinType = coinType,
                candleUnit = candleUnit,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                exchangeCandles = exchangeCandles
            )
            loadExchangeCandle(
                exchangeType = exchangeType,
                coinType = coinType,
                candleUnit = candleUnit,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                exchangeCandles = exchangeCandles
            )

            startDateTime = endDateTime
        }
    }

    private fun loadMissingCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        candleUnit: CandleUnit,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        exchangeCandles: List<ExchangeChartCandle>
    ) {
        // 거래소에서 누락된 캔들 시간
        val exchangeMissingCandleDateTimes = CandleDateTimeUtils.findMissingDateTimes(
            candleUnit = candleUnit,
            from = startDateTime,
            to = endDateTime,
            candleDateTimes = exchangeCandles.map { it.candleDateTimeKst }
        )

        // DB에 이미 저장되어있는 누락 시간
        val missingCandleDateTimes = missingCoinCandleRepository.findAllMissingCoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = candleUnit,
            from = startDateTime,
            to = endDateTime,
        ).map { it.candleDateTimeKst }

        // DB에 저장되어있지 않은 누락 시간
        val missingCandles =
            exchangeMissingCandleDateTimes
                .filter { missingCandleDateTimes.contains(it).not() }
                .map { missingDateTime ->
                    MissingCoinCandle(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        unit = candleUnit,
                        candleDateTimeUtc = missingDateTime.toUtc(),
                        candleDateTimeKst = missingDateTime,
                    )
                }

        // 거래소에 존재하지 않는 누락 캔들 저장
        missingCoinCandleRepository.saveAll(missingCandles)
    }

    private fun loadExchangeCandle(
        exchangeType: ExchangeType,
        coinType: CoinType,
        candleUnit: CandleUnit,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        exchangeCandles: List<ExchangeChartCandle>
    ) {
        // DB에 이미 존재하는 캔들
        val dbCandles = coinCandleRepository.findAllCoinCandle(
            exchangeType = exchangeType,
            coinType = coinType,
            unit = candleUnit,
            from = startDateTime,
            to = endDateTime,
        )
        val dbMissingCandleDateTimes = CandleDateTimeUtils.findMissingDateTimes(
            candleUnit = candleUnit,
            from = startDateTime,
            to = endDateTime,
            candleDateTimes = dbCandles.map { it.candleDateTimeKst }
        )
        // DB에 존재하지 않는 캔들
        val dbMissingCandles =
            exchangeCandles.filter { dbMissingCandleDateTimes.contains(it.candleDateTimeKst) }
                .map {
                    CoinCandle(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        unit = candleUnit,
                        candleDateTimeUtc = it.candleDateTimeUtc,
                        candleDateTimeKst = it.candleDateTimeKst,
                        openingPrice = it.openingPrice,
                        highPrice = it.highPrice,
                        lowPrice = it.lowPrice,
                        closingPrice = it.closingPrice,
                        accTradePrice = it.accTradePrice,
                        accTradeVolume = it.accTradeVolume,
                    )
                }
        // DB에 존재하지 않는 캔들 저장
        coinCandleRepository.saveAll(dbMissingCandles)
    }

    companion object {
        private const val CANDLE_COUNT_BUFFER = 50 // 캔들 유실 케이스 고려
        private const val CALCULATE_INDICATOR_CANDLE_COUNT = 200
        private const val CHART_LOAD_CHUNK_SIZE = 1000
    }
}