package com.dingdongdeng.autotrading.domain.chart.service

import com.dingdongdeng.autotrading.domain.chart.entity.CoinCandle
import com.dingdongdeng.autotrading.domain.chart.model.Candle
import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.chart.repository.CoinCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.factory.IndicatorFactory
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.infra.common.utils.minDate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinChartService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinCandleRepository: CoinCandleRepository,
) {

    fun getCharts(
        exchangeType: ExchangeType,
        keyPairId: String = "",
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
        count: Int,
        to: LocalDateTime,
    ): List<Chart> {
        return AsyncUtils.joinAll(candleUnits) { candleUnit ->
            makeChartProcess(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                coinType = coinType,
                candleUnit = candleUnit,
                from = to.minusSeconds(count * candleUnit.getSecondSize()),
                to = to,
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

            if (endIndex >= exchangeCandles.size) {
                break
            }

            if (exchangeCandles[endIndex].candleDateTimeKst < from) {
                continue
            }

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

        //FIXME 백테는 안터질거같은데 실제 운영에서는 터질듯?? (캔들 누락)
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