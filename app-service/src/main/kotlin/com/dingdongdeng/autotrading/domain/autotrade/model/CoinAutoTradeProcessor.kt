package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.LocalDateTime
import java.util.UUID

class CoinAutoTradeProcessor(
    override val id: String = "${UUID.randomUUID()}",
    override val userId: Long,
    val title: String,
    val strategyType: CoinStrategyType,
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val keyPairId: String,
    val config: Map<String, Any>,
    override val duration: Long = 60 * 1000, // milliseconds
    private val slackSender: SlackSender?,

    private val strategy: SpotCoinStrategy,
    private val chartFinder: (coinType: CoinType, now: LocalDateTime) -> List<Chart>,
    private val tradeExecutor: (processorId: String, task: SpotCoinStrategyTask) -> Unit,
    private val tradeSyncer: (processorId: String, coinType: CoinType) -> List<CoinTradeHistory>,
    private val tradeInfoFinder: (processorId: String, coinType: CoinType, now: LocalDateTime) -> CoinTradeSummary,
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
        tasks.forEach { task -> tradeExecutor(id, task) }
    }

    private fun makeParamProcess(coinType: CoinType): SpotCoinStrategyMakeTaskParam {
        // 차트 조회
        val now = TimeContext.now()
        val charts = chartFinder(coinType, now)

        // 거래 정보 조회
        tradeSyncer(id, coinType)
        val tradeInfo = tradeInfoFinder(id, coinType, now)

        return SpotCoinStrategyMakeTaskParam(
            exchangeType = exchangeType,
            coinType = coinType,
            currentPrice = tradeInfo.currentPrice,
            charts = charts,
            tradeSummary = tradeInfo
        )
    }

    override fun runnable(): Boolean {
        return true
    }
}