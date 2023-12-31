package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.service.IndicatorService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartCandleParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTradeInfoParam
import com.dingdongdeng.autotrading.domain.strategy.service.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.TradeHistoryService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.max

@Service
class CoinAutoTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val indicatorService: IndicatorService,
    private val strategyServices: List<SpotCoinStrategy>,
    private val autoTradeService: AutoTradeService,
    private val tradeHistoryService: TradeHistoryService,
) {

    fun register(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        //TODO strategy에서 사용할 커스텀 파라미터도 추가 필요해
    ): String {

        val autoTradeProcessorId = UUID.randomUUID().toString()
        val strategyService = strategyServices.first { it.support(coinStrategyType) }
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getExchangeKeyPair(keyPairId)

        val process = {
            val makeTaskParams = coinTypes.map { coinType ->
                SpotCoinStrategyMakeTaskParam(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    charts = makeSpotCoinStrategyChartParam(
                        exchangeService = exchangeService,
                        coinType = coinType,
                        candleUnits = candleUnits,
                        exchangeKeyPair = exchangeKeyPair,
                    ),
                    tradeInfo = makeSpotCoinStrategyTradeInfoParam(
                        autoTradeProcessorId = autoTradeProcessorId,
                        coinType = coinType,
                    ),
                )
            }
            strategyService.makeTask(makeTaskParams)
            strategyService.handleResult()
        }

        return autoTradeService.register(autoTradeProcessorId, userId, process, 10000)
    }

    private fun makeSpotCoinStrategyChartParam(
        exchangeService: SpotCoinExchangeService,
        coinType: CoinType,
        candleUnits: List<CandleUnit>,
        exchangeKeyPair: ExchangeKeyPair
    ): List<SpotCoinStrategyChartParam> {
        return candleUnits.map { candleUnit ->
            val now = TimeContext.now()
            val chartParam = SpotCoinExchangeChartParam(
                coinType = coinType,
                candleUnit = candleUnit,
                from = now.minusMinutes(2 * CHART_CANDLE_MAX_COUNT * candleUnit.getMinuteSize()), // 보조지표 계산을 위해 2배로 조회
                to = now,
            )

            val chart = exchangeService.getChart(chartParam, exchangeKeyPair)

            val strategyChartCandleParams = mutableListOf<SpotCoinStrategyChartCandleParam>()
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

                strategyChartCandleParams.add(
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
            strategyChartCandleParams.sortBy { it.candleDateTimeKst }

            SpotCoinStrategyChartParam(
                from = chart.from,
                to = chart.to,
                currentPrice = chart.currentPrice,
                candleUnit = candleUnit,
                candles = strategyChartCandleParams,
            )
        }
    }

    private fun makeSpotCoinStrategyTradeInfoParam(
        autoTradeProcessorId: String,
        coinType: CoinType,
    ): SpotCoinStrategyTradeInfoParam {
        val coinTradeHistories = tradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        val buyCoinTradeHistories = coinTradeHistories.filter { it.orderType == OrderType.BUY }
        val sellCoinTradeHistories = coinTradeHistories.filter { it.orderType == OrderType.SELL }

        val volume = buyCoinTradeHistories.sumOf { it.volume } - sellCoinTradeHistories.sumOf { it.volume }
        val value =
            buyCoinTradeHistories.sumOf { it.price * it.volume } - sellCoinTradeHistories.sumOf { it.price * it.volume }
        val averagePrice = if (volume == 0.0) 0 else value / volume

        return SpotCoinStrategyTradeInfoParam(
            volume = buyCoinTradeHistories.sumOf { it.volume } - sellCoinTradeHistories.sumOf { it.volume },
            averagePrice = averagePrice.toInt(),
            valuePrice = 0,
            valueProfitPrice = 0,
            realizedProfitPrice = 0, // TODO
            coinTradeHistory = tradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        )
    }

    companion object {
        const val CHART_CANDLE_MAX_COUNT = 200
    }
}