package com.dingdongdeng.autotrading.domain.autotrade.factory

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Component

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
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>,
        coinStrategyType: CoinStrategyType,
    ): CoinAutoTradeProcessor {
        return CoinAutoTradeProcessor(
            userId = userId,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
            duration = 60_000,
            slackSender = slackSender,
            strategy = strategies.first { it.support(coinStrategyType) },
            coinChartService = coinChartService,
            coinTradeService = coinTradeService,
        )
    }
}