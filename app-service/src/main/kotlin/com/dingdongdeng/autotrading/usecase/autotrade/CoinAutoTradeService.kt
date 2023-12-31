package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.indicator.service.IndicatorService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartCandleParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTradeInfoParam
import com.dingdongdeng.autotrading.domain.strategy.service.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeHistoryService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
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
    private val coinTradeHistoryService: CoinTradeHistoryService,
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
                val charts = makeSpotCoinStrategyChartParam(
                    exchangeService = exchangeService,
                    coinType = coinType,
                    candleUnits = candleUnits,
                    exchangeKeyPair = exchangeKeyPair,
                )
                SpotCoinStrategyMakeTaskParam(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    charts = charts,
                    tradeInfo = makeSpotCoinStrategyTradeInfoParam(
                        exchangeService = exchangeService,
                        exchangeKeyPair = exchangeKeyPair,
                        autoTradeProcessorId = autoTradeProcessorId,
                        coinType = coinType,
                        currentPrice = charts.first().currentPrice,
                    ),
                )
            }
            strategyService.makeTask(makeTaskParams).forEach { task ->
                val orderResponse = when (task.orderType) {
                    OrderType.BUY, OrderType.SELL -> {
                        val param = SpotCoinExchangeOrderParam(
                            coinType = task.coinType,
                            orderType = task.orderType,
                            volume = task.volume,
                            price = task.price,
                            priceType = task.priceType,
                        )
                        exchangeService.order(param, exchangeKeyPair)
                    }

                    OrderType.CANCEL -> exchangeService.cancel(task.orderId!!, exchangeKeyPair)
                }

                coinTradeHistoryService.save(
                    makeCoinTradeHistory(
                        order = orderResponse,
                        autoTradeProcessorId = autoTradeProcessorId
                    )
                )
            }
        }

        return autoTradeService.register(autoTradeProcessorId, userId, process, 10000)
    }

    private fun makeSpotCoinStrategyChartParam(
        exchangeService: SpotCoinExchangeService,
        exchangeKeyPair: ExchangeKeyPair,
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
        exchangeService: SpotCoinExchangeService,
        exchangeKeyPair: ExchangeKeyPair,
        autoTradeProcessorId: String,
        coinType: CoinType,
        currentPrice: Double,
    ): SpotCoinStrategyTradeInfoParam {

        // WAIT 상태의 거래건들 업데이트
        val tradeHistories2 = coinTradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        val waitTradeHistories = tradeHistories2.filter { it.state == TradeState.WAIT }
        waitTradeHistories.forEach { waitTradeHistory ->
            val order = exchangeService.getOrder(waitTradeHistory.orderId, exchangeKeyPair)
            coinTradeHistoryService.save(makeCoinTradeHistory(waitTradeHistory.id, order, autoTradeProcessorId))
        }

        val tradeHistories = coinTradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        val buyTradeHistories = tradeHistories.filter { it.orderType == OrderType.BUY }
        val sellTradeHistories = tradeHistories.filter { it.orderType == OrderType.SELL }

        val volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume }
        val value = buyTradeHistories.sumOf { it.price * it.volume } - sellTradeHistories.sumOf { it.price * it.volume }
        val averagePrice = if (volume == 0.0) 0.0 else (value / volume)
        val valuePrice = (volume * currentPrice)
        val originPrice = (volume * averagePrice)

        return SpotCoinStrategyTradeInfoParam(
            volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume },
            averagePrice = averagePrice,
            valuePrice = valuePrice,
            originPrice = originPrice,
            profitPrice = (valuePrice - originPrice),
            coinTradeHistory = coinTradeHistoryService.findAllTradeHistory(autoTradeProcessorId, coinType)
        )
    }

    private fun makeCoinTradeHistory(
        coinTradehistoryId: Long? = null,
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String
    ): CoinTradeHistory {
        return CoinTradeHistory(
            id = coinTradehistoryId,
            orderId = order.orderId,
            state = order.tradeState,
            autoTradeProcessorId = autoTradeProcessorId,
            exchangeType = order.exchangeType,
            coinType = order.coinType,
            orderType = order.orderType,
            priceType = order.priceType,
            volume = order.volume,
            price = order.price,
            fee = order.fee,
            tradedAt = if (order.orderType == OrderType.CANCEL) order.cancelDateTime!! else order.orderDateTime!!,
        )
    }

    companion object {
        const val CHART_CANDLE_MAX_COUNT = 200
    }
}