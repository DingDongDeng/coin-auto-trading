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
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.max

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
    ): List<Chart> {
        return candleUnits.map { candleUnit ->
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
        /*
         * 각 캔들의 보조지표 계산을 위한 200개씩의 과거 캔들이 필요
         * 아래 로직은 총 600개의 캔들을 미리 조회하고
         * 리스트를 200크기로 subList하여 실제 매매에서 사용할 캔들(보조지표가 계산된) 200개를 생성
         * ex)
         *  idx= 0~199 캔들은 idx=199 캔들의 보조 지표 계산해 사용
         *  idx= 1~200 캔들은 idx=200 캔들의 보조 지표 계산해 사용
         *  ... (생략)
         *  idx= 200~399 캔들은 idx=399 캔들의 보조 지표 계산해 사용
         *  실제 매매 로직에서는 보조지표가 계산된 idx=199~399를 사용
         */
        val chart = exchangeService.getChart(chartParam, exchangeKeyPair)
        val candles = mutableListOf<Candle>()
        var count = 0
        //TODO 여기가 리소르를 너무써... 캔들 400개만 세팅해주고 필요할때 계산하게 만들자
        for ((index, candle) in chart.candles.reversed().withIndex()) {
            if (count >= CHART_CANDLE_MAX_COUNT) {
                break
            }

            // startIdx <= subList < endIdx
            val startIdx = max(
                0,
                chart.candles.size - CHART_CANDLE_MAX_COUNT - index
            ) // 무조건 0 이상의 수가 나오도록 방어 로직
            val endIdx = chart.candles.size - index
            if (endIdx - startIdx < 200) {
                throw RuntimeException("캔들의 보조 지표 계산을 위한 적절한 수의 과거 캔들을 추출하는데 실패")
            }
            val indicator = indicatorService.calculate(chart.candles.subList(startIdx, endIdx))
            if (candle.candleDateTimeKst.isEqual(indicator.indicatorDateTimeKst).not()) {
                throw RuntimeException("캔들의 시간과 보조지표의 시간이 다름 (예상한 계산 결과가 아님)")
            }

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
                    // TODO indicator 어쩔까?
                )
            )
            count++
        }
        candles.sortBy { it.candleDateTimeKst }

        return Chart(
            from = chart.from,
            to = chart.to,
            currentPrice = chart.currentPrice,
            candleUnit = candleUnit,
            candles = candles,
        )
    }

    companion object {
        const val CHART_CANDLE_MAX_COUNT = 200
    }
}