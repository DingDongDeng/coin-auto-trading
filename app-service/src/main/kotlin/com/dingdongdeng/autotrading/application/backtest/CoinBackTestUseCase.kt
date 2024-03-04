package com.dingdongdeng.autotrading.application.backtest

import com.dingdongdeng.autotrading.domain.backtest.factory.BackTestProcessorFactory
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
    private val backTestProcessorFactory: BackTestProcessorFactory,
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
        // 백테스트 실행
        val processor = backTestProcessorFactory.of(
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            userId = userId,
            coinStrategyType = coinStrategyType,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
        )
        processRepository.save(processor)
        processor.start()
        return processor.id
    }
}