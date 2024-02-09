package com.dingdongdeng.autotrading.domain.chart.service

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.model.Candle
import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.chart.repository.CoinCandleRepository
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
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinChartService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val indicatorService: IndicatorService,
    private val coinCandleRepository: CoinCandleRepository,
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
            throw RuntimeException("캔들의 보조 지표 계산을 위한 적절한 수의 과거 캔들을 추출하는데 실패")
        }

        return Chart(
            from = exchangeChart.from,
            to = exchangeChart.to,
            currentPrice = exchangeChart.currentPrice,
            candleUnit = candleUnit,
            candles = candles,
        )
    }

    fun loadChartProcess(
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
         *  1. DB에 해당 범위에 없는 캔들을 특정한다. (ExchangeUtils.findMissingCandles())
         *  2. 거래소에서 캔들을 조회한다
         *  3. DB에 없는 캔들들을 저장한다.
         */
        var startDateTime = from
        while (true) {
            if (startDateTime.isAfter(to)) {
                break
            }
            val endDateTime = startDateTime.plusSeconds(candleUnit.getSecondSize() * CHART_LOAD_CHUNK_SIZE)

            // DB에 이미 존재하는 캔들
            val dbCandles = coinCandleRepository.findAllExchangeCandle(
                exchangeType = exchangeType,
                coinType = coinType,
                unit = candleUnit,
                from = startDateTime,
                to = endDateTime,
            )

            // DB에 존재하지 않는 캔들
            val exchangeChart = exchangeService.getChart(
                SpotCoinExchangeChartParam(coinType, candleUnit, startDateTime, endDateTime),
                keyParam
            )
            val missingCandleDateTimes = CandleDateTimeUtils.findMissingDateTimes(
                candleUnit = candleUnit,
                from = startDateTime,
                to = endDateTime,
                candleDateTimes = dbCandles.map { it.candleDateTimeKst }
            )
            val missingCandles = exchangeChart.candles.filter { missingCandleDateTimes.contains(it.candleDateTimeKst) }
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
            coinCandleRepository.saveAll(missingCandles)
            startDateTime = endDateTime
        }
    }

    companion object {
        private const val CHART_CANDLE_MAX_COUNT = 200
        private const val CHART_LOAD_CHUNK_SIZE = 1000
    }
}