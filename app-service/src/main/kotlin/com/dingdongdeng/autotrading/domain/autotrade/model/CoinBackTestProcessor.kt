package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.domain.strategy.service.CoinStrategyService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import java.time.LocalDateTime
import java.util.UUID

class CoinBackTestProcessor(
    override val id: String = "BACKTEST-${UUID.randomUUID()}",
    override val userId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격

    val coinStrategyType: CoinStrategyType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val config: Map<String, Any>,
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
    private val coinStrategyService: CoinStrategyService,
) : Processor(
    id = id,
    userId = userId,
    duration = 0,
    slackSender = null,
) {
    private val autoTradeProcessor: Processor = CoinAutoTradeProcessor(
        id = id,
        userId = userId,
        coinStrategyType = coinStrategyType,
        exchangeType = ExchangeType.BACKTEST,
        coinTypes = coinTypes,
        candleUnits = candleUnits,
        keyPairId = "",
        config = config,
        duration = 0,
        slackSender = null,
        coinChartService = coinChartService,
        coinTradeService = coinTradeService,
        coinStrategyService = coinStrategyService,
    )
    private var initialize = false

    override fun process() {
        autoTradeProcessor.process()
    }

    override fun runnable(): Boolean {
        if (initialize.not()) {
            TimeContext.update { startDateTime }
            initialize = true
        }
        val now = TimeContext.now().plusSeconds(durationUnit.getSecondSize())
        TimeContext.update { now }
        return now < endDateTime
    }
}