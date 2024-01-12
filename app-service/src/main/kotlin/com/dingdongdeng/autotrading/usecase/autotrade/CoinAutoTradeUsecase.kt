package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.Usecase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.usecase.autotrade.service.AutoTradeManageService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeChartService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeInfoService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeTaskService
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

        val autoTradeProcessorId = UUID.randomUUID().toString()

        val process = {
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
                    autoTradeProcessorId = autoTradeProcessorId,
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
                autoTradeProcessorId = autoTradeProcessorId,
                exchangeType = exchangeType
            )
        }

        // 자동매매 등록
        return autoTradeManageService.register(
            userId = userId,
            autoTradeProcessorId = autoTradeProcessorId,
            process = process,
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
}