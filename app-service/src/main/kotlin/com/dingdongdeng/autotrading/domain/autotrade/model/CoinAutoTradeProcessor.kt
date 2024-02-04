package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.chart.service.ChartService
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.service.CoinStrategyService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.util.UUID

class CoinAutoTradeProcessor(
    override val id: String = "AUTOTRADE-${UUID.randomUUID()}",
    override val userId: Long,
    val coinStrategyType: CoinStrategyType,
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val keyPairId: String,
    val config: Map<String, Any>,
    private val duration: Long = 60 * 1000, // milliseconds
    private val slackSender: SlackSender?,

    private val chartService: ChartService,
    private val coinTradeService: CoinTradeService,
    private val coinStrategyService: CoinStrategyService,
) : Processor(
    id = id,
    userId = userId,
    duration = duration,
    slackSender = slackSender,
) {
    override fun process() {
        val params = coinTypes.map { coinType ->
            // 차트 조회
            val charts = chartService.getCharts(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                coinType = coinType,
                candleUnits = candleUnits,
            )

            // 거래 정보 조회
            val tradeInfo = coinTradeService.getTradeInfo(
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                autoTradeProcessorId = id,
                coinType = coinType,
                currentPrice = charts.first().currentPrice,
            )

            SpotCoinStrategyMakeTaskParam(
                exchangeType = exchangeType,
                coinType = coinType,
                charts = charts,
                tradeInfo = tradeInfo,
            )
        }

        // 전략을 수행할 task 생성
        val tasks = coinStrategyService.getTask(
            params = params,
            config = config,
            strategyType = coinStrategyType,
            keyPairId = keyPairId,
            autoTradeProcessorId = id,
            exchangeType = exchangeType
        )

        // 거래 (매수, 매도, 취소)
        tasks.forEach { task ->
            coinTradeService.trade(
                orderId = task.orderId,
                autoTradeProcessorId = id,
                exchangeType = exchangeType,
                keyPairId = keyPairId,
                orderType = task.orderType,
                coinType = task.coinType,
                volume = task.volume,
                price = task.price,
                priceType = task.priceType,
            )
        }
    }

    override fun runnable(): Boolean {
        return true
    }
}