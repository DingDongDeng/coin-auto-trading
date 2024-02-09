package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import com.dingdongdeng.autotrading.domain.chart.service.ChartService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.Usecase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import java.time.LocalDateTime

@Usecase
class CoinAutoTradeUsecase(
    private val autoTradeService: AutoTradeService,
    private val chartService: ChartService,
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
        // 자동매매 등록
        return autoTradeService.register(
            userId = userId,
            coinStrategyType = coinStrategyType,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
        )
    }

    fun backTest(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        userId: Long,
        coinStrategyType: CoinStrategyType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>
    ): String {
        // 백테스트 실행
        return autoTradeService.backTest(
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            userId = userId,
            coinStrategyType = coinStrategyType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
        )
    }

    fun loadCharts(
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
    ) {
        AsyncUtils.joinAll(coinTypes) { coinType ->
            chartService.loadCharts(
                coinType = coinType,
                keyPairId = keyPairId,
                exchangeType = exchangeType,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                candleUnits = candleUnits,
            )
        }
    }

    fun start(autoTradeProcessorId: String): String {
        autoTradeService.start(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun stop(autoTradeProcessorId: String): String {
        autoTradeService.stop(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun terminate(autoTradeProcessorId: String): String {
        autoTradeService.terminate(autoTradeProcessorId)
        return autoTradeProcessorId
    }
}