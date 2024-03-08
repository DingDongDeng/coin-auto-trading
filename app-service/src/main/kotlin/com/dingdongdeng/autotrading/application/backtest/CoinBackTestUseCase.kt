package com.dingdongdeng.autotrading.application.backtest

import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.factory.BackTestProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.repository.ProcessRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
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
    private val coinTradeService: CoinTradeService,
    private val coinChartService: CoinChartService,
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
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            autoTradeProcessor = autoTradeProcessor,
        )
        processRepository.save(backTestProcessor)
        backTestProcessor.start()
        return backTestProcessor.id
    }

    fun getResult(backTestProcessorId: String) {
        val processor = processRepository.findById(backTestProcessorId) as CoinBackTestProcessor

        val backTestResults = processor.getResults()
        /**
         * FIXME 아래 내용을 해보자
         *  화면을 먼저 기획해보자
         *  - 내 벡테스트 목록을 조회
         *  - 백테스트 결과 조회
         *      - 월별 수익율
         *          - 코인별 수익율
         *      - 월별 승율
         *          - 코인별 승율
         *      - 주문 리스트 (매수, 익절, 손절)
         *      - 시물레이션한 차트 리스트
         *          - 캔들 정보
         *          - 보조지표 정보
         *          - 주문정보
         */
    }

    fun getSimulation() {
        // FIXME 차트를 그리기 위한 api...
    }
}