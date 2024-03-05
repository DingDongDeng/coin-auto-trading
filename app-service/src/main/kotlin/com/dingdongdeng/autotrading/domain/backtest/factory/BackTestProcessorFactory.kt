package com.dingdongdeng.autotrading.domain.backtest.factory

import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BackTestProcessorFactory(
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
    private val strategies: List<SpotCoinStrategy>,
) {

    fun of(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>,
    ): CoinBackTestProcessor {
        return CoinBackTestProcessor(
            userId = userId,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
            strategy = strategies.first { it.support(coinStrategyType) },
            coinChartService = coinChartService,
            coinTradeService = coinTradeService,
        )
    }
}