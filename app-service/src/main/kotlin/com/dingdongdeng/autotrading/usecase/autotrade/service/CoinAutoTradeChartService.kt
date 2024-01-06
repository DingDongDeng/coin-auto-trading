package com.dingdongdeng.autotrading.usecase.autotrade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.service.IndicatorService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartCandleParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartParam
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class CoinAutoTradeChartService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val indicatorService: IndicatorService,
) {

    fun makeCharts(
        exchangeType: ExchangeType,
        keyPairId: String,
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
    ): List<SpotCoinStrategyChartParam> {
        return candleUnits.map { candleUnit ->
            val now = TimeContext.now()
            val chartParam = SpotCoinExchangeChartParam(
                coinType = coinType,
                candleUnit = candleUnit,
                from = now.minusMinutes(2 * CHART_CANDLE_MAX_COUNT * candleUnit.getMinuteSize()), // 보조지표 계산을 위해 2배로 조회
                to = now,
            )

            val exchangeService = exchangeServices.first { it.support(exchangeType) }
            val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
            val chart = exchangeService.getChart(chartParam, exchangeKeyPair)

            val chartCandleParams = mutableListOf<SpotCoinStrategyChartCandleParam>()
            var count = 0
            for ((index, candle) in chart.candles.withIndex().reversed()) {
                if (count >= CHART_CANDLE_MAX_COUNT) {
                    break
                }
                val indicator = indicatorService.calculate(
                    chart.candles.subList(
                        max(0, chart.candles.size - CHART_CANDLE_MAX_COUNT - index),
                        chart.candles.size - 1 - index
                    )
                )
                if (candle.candleDateTimeKst.isEqual(indicator.indicatorDateTimeKst).not()) {
                    throw RuntimeException("캔들의 시간과 보조지표의 시간이 다름 (예상한 계산 결과가 아님)")
                }

                chartCandleParams.add(
                    SpotCoinStrategyChartCandleParam(
                        candleUnit = candle.candleUnit,
                        candleDateTimeUtc = candle.candleDateTimeUtc,
                        candleDateTimeKst = candle.candleDateTimeKst,
                        openingPrice = candle.openingPrice,
                        highPrice = candle.highPrice,
                        lowPrice = candle.lowPrice,
                        closingPrice = candle.closingPrice,
                        accTradePrice = candle.accTradePrice,
                        accTradeVolume = candle.accTradeVolume,
                        indicators = indicator,
                    )
                )
                count++
            }
            chartCandleParams.sortBy { it.candleDateTimeKst }

            SpotCoinStrategyChartParam(
                from = chart.from,
                to = chart.to,
                currentPrice = chart.currentPrice,
                candleUnit = candleUnit,
                candles = chartCandleParams,
            )
        }
    }

    companion object {
        const val CHART_CANDLE_MAX_COUNT = 200
    }
}