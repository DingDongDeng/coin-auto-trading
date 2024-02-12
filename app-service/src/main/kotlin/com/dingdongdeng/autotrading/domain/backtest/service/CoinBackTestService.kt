package com.dingdongdeng.autotrading.domain.backtest.service

import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.service.ProcessService
import com.dingdongdeng.autotrading.domain.strategy.service.CoinStrategyService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinBackTestService(
    private val processService: ProcessService,
    private val coinChartService: CoinChartService,
    private val coinTradeService: CoinTradeService,
    private val coinStrategyService: CoinStrategyService,
) {

    fun backTest(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        userId: Long,
        coinStrategyType: CoinStrategyType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>,
    ): String {
        //FIXME 1분봉에 대한 누락 체크?
        val backTestProcessor = CoinBackTestProcessor(
            userId = userId,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            coinStrategyType = coinStrategyType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
            coinChartService = coinChartService,
            coinTradeService = coinTradeService,
            coinStrategyService = coinStrategyService,
        )
        processService.register(backTestProcessor)
        processService.start(backTestProcessor.id)
        return backTestProcessor.id
    }
}