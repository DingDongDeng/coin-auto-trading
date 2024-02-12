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
import com.dingdongdeng.autotrading.domain.indicator.service.IndicatorService
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
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
    private val indicatorService: IndicatorService,
    private val coinCandleRepository: CoinCandleRepository,
    private val missingCoinCandleRepository: MissingCoinCandleRepository,
) {

    fun getCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
    ): List<Chart> {
        return AsyncUtils.joinAll(candleUnits) { candleUnit ->
            makeChartProcess(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                coinType = coinType,
                candleUnit = candleUnit,
            )
        }
    }

    fun loadCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
    ) {
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
    ): Chart {
        val now = TimeContext.now()
        val chartParam = SpotCoinExchangeChartParam(
            coinType = coinType,
            candleUnit = candleUnit,
            // 보조지표 계산을 위해 3배로 조회(버퍼)
            from = now.minusSeconds(3 * CHART_CANDLE_MAX_COUNT * candleUnit.getSecondSize()),
            to = now,
        )
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        val exchangeChart = exchangeService.getChart(chartParam, exchangeKeyPair)
        val candles = mutableListOf<Candle>()

        val exchangeCandles = exchangeChart.candles
            .sortedBy { it.candleDateTimeKst } // 혹시 모르니 한번 더 정렬
            .takeLast(400) // 마지막 400개
        for (i in 0..200) {
            val startIndex = i
            val endIndex = startIndex + CHART_CANDLE_MAX_COUNT - 1

            val subCandles = exchangeCandles.subList(startIndex, endIndex + 1) // 참조만 변경 (deep copy x)

            if (subCandles.size < CHART_CANDLE_MAX_COUNT) {
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
                    indicators = { indicatorService.calculate(subCandles) }
                )
            )
        }

        if (candles.size < CHART_CANDLE_MAX_COUNT) {
            throw CriticalException.of("캔들의 보조 지표 계산을 위한 적절한 수의 과거 캔들을 추출하는데 실패")
        }

        return Chart(
            from = exchangeChart.from,
            to = exchangeChart.to,
            currentPrice = exchangeChart.currentPrice,
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
            val endDateTime = minDate(
                startDateTime.plusSeconds(candleUnit.getSecondSize() * CHART_LOAD_CHUNK_SIZE),
                to
            )

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
        private const val CHART_CANDLE_MAX_COUNT = 200
        private const val CHART_LOAD_CHUNK_SIZE = 1000
    }
}