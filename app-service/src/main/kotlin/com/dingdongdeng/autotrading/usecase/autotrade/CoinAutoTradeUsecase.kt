package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.CoinAutoTradeService
import com.dingdongdeng.autotrading.domain.backtest.service.CoinBackTestService
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.Usecase
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import java.time.LocalDateTime

@Usecase
class CoinAutoTradeUsecase(
    private val coinAutoTradeService: CoinAutoTradeService,
    private val coinBackTestService: CoinBackTestService,
    private val coinChartService: CoinChartService,
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
        return coinAutoTradeService.register(
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
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>
    ): String {
        // 백테스트 실행
        return coinBackTestService.backTest(
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
    }

    fun loadCharts(
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
    ) {
        if (candleUnits.contains(CandleUnit.min()).not()) {
            throw WarnException.of("거래소의 캔들을 서버에 다운로드 할때는 가장 작은 캔들 단위를 포함해야만 합니다. min=${CandleUnit.min()}")
        }
        AsyncUtils.joinAll(coinTypes) { coinType ->
            coinChartService.loadCharts(
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
        coinAutoTradeService.start(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun stop(autoTradeProcessorId: String): String {
        coinAutoTradeService.stop(autoTradeProcessorId)
        return autoTradeProcessorId
    }

    fun terminate(autoTradeProcessorId: String): String {
        coinAutoTradeService.terminate(autoTradeProcessorId)
        return autoTradeProcessorId
    }
}