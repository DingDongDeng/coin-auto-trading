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
        val exchangeCandles = getCandlesByCount(
            exchangeType = exchangeType,
            keyPairId = keyPairId,
            coinType = coinType,
            candleUnit = candleUnit,
            to = to,
            candleCount = 2 * CALCULATE_INDICATOR_CANDLE_COUNT,
        )

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

        return Chart(
            from = candles.first().candleDateTimeKst,
            to = candles.last().candleDateTimeKst,
            candleUnit = candleUnit,
            candles = candles,
        )
    }

    private fun getCandlesByCount(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnit: CandleUnit,
        to: LocalDateTime,
        candleCount: Int,
    ): List<ExchangeChartCandle> {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        var candles = emptyList<ExchangeChartCandle>()
        var loopCnt = 0
        // 필요한 캔들 숫자가 조회될때까지 반복
        while (candles.size < candleCount) {
            val endDateTime = to.minusSeconds(candleUnit.getSecondSize() * candleCount * loopCnt++) // 루프마다 기준점이 달라짐
            val startDateTime = endDateTime.minusSeconds(candleUnit.getSecondSize() * candleCount)
            val chartParam2 = SpotCoinExchangeChartParam(
                coinType = coinType,
                candleUnit = candleUnit,
                from = startDateTime,
                to = endDateTime,
            )
            candles = candles + exchangeService.getChart(chartParam2, exchangeKeyPair).candles
        }

        return candles.takeLast(candleCount)
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
        private const val CALCULATE_INDICATOR_CANDLE_COUNT = 200
        private const val CHART_LOAD_CHUNK_SIZE = 1000
    }
}