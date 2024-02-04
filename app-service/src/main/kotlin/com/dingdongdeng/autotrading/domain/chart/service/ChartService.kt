package com.dingdongdeng.autotrading.domain.chart.service

import com.dingdongdeng.autotrading.domain.chart.model.Candle
import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.service.IndicatorService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChartService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val indicatorService: IndicatorService,
) {

    fun getCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
    ): List<Chart> = runBlocking {
        candleUnits.map { candleUnit ->
            async {
                makeChartProcess(
                    exchangeType = exchangeType,
                    keyPairId = keyPairId,
                    coinType = coinType,
                    candleUnit = candleUnit,
                )
            }
        }.awaitAll()
    }

    fun loadCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
    ) {
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val keyPair = exchangeService.getKeyPair(keyPairId)
        candleUnits.forEach { candleUnit ->
            exchangeService.loadChart(
                param = SpotCoinExchangeChartParam(
                    coinType = coinType,
                    candleUnit = candleUnit,
                    from = startDateTime,
                    to = endDateTime,
                ),
                keyParam = keyPair,
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
            from = now.minusMinutes(3 * CHART_CANDLE_MAX_COUNT * candleUnit.getMinuteSize()),
            to = now,
        )
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
        val chart = exchangeService.getChart(chartParam, exchangeKeyPair)
        val candles = mutableListOf<Candle>()
        //FIXME 병렬로 계산시켜보자
        chart.candles
            .sortedBy { it.candleDateTimeKst } // 혹시 모르니 한번 더 정렬
            .takeLast(400) // 마지막 400개
            .windowed(CHART_CANDLE_MAX_COUNT, 1, true) { subList ->
                if (subList.size < CHART_CANDLE_MAX_COUNT) {
                    return@windowed
                }
                val subCandles = subList.toList() //deep copy
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
            from = chart.from,
            to = chart.to,
            currentPrice = chart.currentPrice,
            candleUnit = candleUnit,
            candles = candles,
        )
    }

    companion object {
        private const val CHART_CANDLE_MAX_COUNT = 200
    }
}