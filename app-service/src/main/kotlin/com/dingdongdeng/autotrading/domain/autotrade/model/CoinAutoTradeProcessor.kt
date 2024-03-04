package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
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

    private val strategy: SpotCoinStrategy,
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
) : Processor(
    id = id,
    userId = userId,
    duration = duration,
    slackSender = slackSender,
) {
    override fun process() {
        // 병렬 수행
        val params = AsyncUtils.joinAll(coinTypes) { coinType -> makeParamProcess(coinType) }

        // 전략을 수행할 task 생성
        val tasks = strategy.makeTask(params, config)

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

    private fun makeParamProcess(coinType: CoinType): SpotCoinStrategyMakeTaskParam {
        // 차트 조회
        val charts = coinChartService.getCharts(
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

        return SpotCoinStrategyMakeTaskParam(
            exchangeType = exchangeType,
            coinType = coinType,
            charts = charts,
            tradeInfo = tradeInfo,
        )
    }

    override fun runnable(): Boolean {
        return true
    }
}