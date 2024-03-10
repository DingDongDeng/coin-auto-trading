package com.dingdongdeng.autotrading.application.backtest

import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.factory.BackTestProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestResult
import com.dingdongdeng.autotrading.domain.backtest.service.CoinBackTestService
import com.dingdongdeng.autotrading.domain.process.repository.ProcessRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.time.LocalDateTime

@UseCase
class CoinBackTestUseCase(
    private val processRepository: ProcessRepository,
    private val autoTradeProcessorFactory: AutoTradeProcessorFactory,
    private val backTestProcessorFactory: BackTestProcessorFactory,
    private val coinBackTestService: CoinBackTestService,
) {
    fun backTest(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>
    ): String {
        val autoTradeProcessor = autoTradeProcessorFactory.of(
            userId = userId,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = "",
            config = config,
            coinStrategyType = coinStrategyType,
        )
        val backTestProcessor = backTestProcessorFactory.of(
            exchangeType = exchangeType,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            autoTradeProcessor = autoTradeProcessor,
        )
        processRepository.save(backTestProcessor)
        backTestProcessor.start()
        return backTestProcessor.id
    }

    fun getResult(backTestProcessorId: String): CoinBackTestResult {
        val processor = processRepository.findById(backTestProcessorId) as CoinBackTestProcessor
        return coinBackTestService.getResults(processor)
    }
}