package com.dingdongdeng.autotrading.domain.autotrade.factory

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AutoTradeProcessorFactory(
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
    private val strategies: List<SpotCoinStrategy>,
    private val slackSender: SlackSender,
) {
    //FIXME 마켓 조회했을때 종목 상태가 warn이면 exception 던지자
    fun of(
        userId: Long,
        title: String,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String = "",
        config: Map<String, Any>,
        coinStrategyType: CoinStrategyType,
    ): CoinAutoTradeProcessor {
        // 차트 정보 조회
        val chartFinder = { coinType: CoinType, now: LocalDateTime ->
            coinChartService.getCharts(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                coinType = coinType,
                candleUnits = candleUnits,
                count = CHART_CANDLE_COUNT,
                to = now,
            )
        }

        // 주문 실행
        val tradeExecutor = { processorId: String, task: SpotCoinStrategyTask ->
            coinTradeService.trade(
                orderId = task.orderId,
                autoTradeProcessorId = processorId,
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                orderType = task.orderType,
                coinType = task.coinType,
                volume = task.volume,
                price = task.price,
                priceType = task.priceType,
            )
        }

        // 주문 상태 동기화
        val tradeSyncer = { processorId: String, coinType: CoinType ->
            coinTradeService.syncTradeHistories(exchangeType, keyPairId, processorId, coinType)
        }

        // 거래 정보 조회
        val tradeInfoFinder = { processorId: String, coinType: CoinType, now: LocalDateTime ->
            coinTradeService.getTradeSummary(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                autoTradeProcessorId = processorId,
                coinType = coinType,
                now = now,
            )
        }

        return CoinAutoTradeProcessor(
            userId = userId,
            title = title,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            config = config,
            duration = 60_000,
            keyPairId = keyPairId,
            slackSender = slackSender,
            strategyType = coinStrategyType,
            strategy = strategies.first { it.support(coinStrategyType) },
            chartFinder = chartFinder,
            tradeExecutor = tradeExecutor,
            tradeSyncer = tradeSyncer,
            tradeInfoFinder = tradeInfoFinder,
        )
    }

    companion object {
        private const val CHART_CANDLE_COUNT = 200
    }
}