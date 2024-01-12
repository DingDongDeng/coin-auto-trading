package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.Usecase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.usecase.autotrade.service.AutoTradeManageService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeChartService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeInfoService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeTaskService
import java.time.LocalDateTime
import java.util.*

@Usecase
class CoinAutoTradeUsecase(
    private val autoTradeManageService: AutoTradeManageService,
    private val coinAutoTradeChartService: CoinAutoTradeChartService,
    private val coinAutoTradeInfoService: CoinAutoTradeInfoService,
    private val coinAutoTradeTaskService: CoinAutoTradeTaskService,
) {

    fun register(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>
    ): String {
        val autoTradeProcessorId = "AUTOTRADE-${UUID.randomUUID()}"

        val process = makeProcess(
            processorId = autoTradeProcessorId,
            coinStrategyType = coinStrategyType,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
        )

        // 자동매매 등록
        return autoTradeManageService.register(
            userId = userId,
            autoTradeProcessorId = autoTradeProcessorId,
            isRunnable = { true },
            process = process,
            duration = 10_000,
        )
    }

    fun backTest(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        duration: Long, // 백테스트 시간 간격(초)
        userId: Long,
        coinStrategyType: CoinStrategyType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>
    ): String {
        val backTestProcessorId = "BACKTEST-${UUID.randomUUID()}"

        val process = makeProcess(
            processorId = backTestProcessorId,
            coinStrategyType = coinStrategyType,
            exchangeType = ExchangeType.BACKTEST,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
        )

        var initialize = false
        val isRunnable = {
            if (initialize.not()) {
                TimeContext.update { startDateTime }
                initialize = true
            }
            val now = TimeContext.now().plusSeconds(duration)
            TimeContext.update { now }
            now.isBefore(endDateTime)
        }

        // 백테스트 등록
        return autoTradeManageService.register(
            userId = userId,
            autoTradeProcessorId = backTestProcessorId,
            isRunnable = isRunnable,
            process = process,
            duration = 0,
        )
    }

    fun start(autoTradeProcessorId: String): String {
        autoTradeManageService.start(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun stop(autoTradeProcessorId: String): String {
        autoTradeManageService.stop(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun terminate(autoTradeProcessorId: String): String {
        autoTradeManageService.terminate(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    private fun makeProcess(
        processorId: String,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>
    ): () -> Unit {
        return {
            val params = coinTypes.map { coinType ->
                // 차트 조회
                val charts = coinAutoTradeChartService.makeCharts(
                    exchangeType = exchangeType,
                    keyPairId = keyPairId,
                    coinType = coinType,
                    candleUnits = candleUnits,
                )

                // 거래 정보 조회
                val tradeInfo = coinAutoTradeInfoService.makeTradeInfo(
                    exchangeType = exchangeType,
                    keyPairId = keyPairId,
                    autoTradeProcessorId = processorId,
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

            // 작업 생성 (매수, 매도, 취소)
            val tasks = coinAutoTradeTaskService.makeTask(
                params = params,
                config = config,
                strategyType = coinStrategyType
            )

            // 작업 실행
            coinAutoTradeTaskService.executeTask(
                tasks = tasks,
                keyPairId = keyPairId,
                autoTradeProcessorId = processorId,
                exchangeType = exchangeType
            )
        }
    }
}