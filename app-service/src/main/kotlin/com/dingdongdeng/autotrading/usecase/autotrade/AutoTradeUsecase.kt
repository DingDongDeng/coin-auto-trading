package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.usecase.autotrade.service.AutoTradeChartService
import com.dingdongdeng.autotrading.usecase.autotrade.service.AutoTradeInfoService
import com.dingdongdeng.autotrading.usecase.autotrade.service.AutoTradeManageService
import com.dingdongdeng.autotrading.usecase.autotrade.service.CoinAutoTradeTaskService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AutoTradeUsecase(
    // 공통
    private val autoTradeManageService: AutoTradeManageService,
    private val autoTradeChartService: AutoTradeChartService,
    private val autoTradeInfoService: AutoTradeInfoService,

    // 코인
    private val coinAutoTradeTaskService: CoinAutoTradeTaskService,
) {

    fun registerCoinAutoTrade(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        //TODO strategy에서 사용할 커스텀 파라미터도 추가 필요해
    ): String {

        val autoTradeProcessorId = UUID.randomUUID().toString()

        val process = {
            val params = coinTypes.map { coinType ->
                // 차트 조회
                val charts = autoTradeChartService.makeCoinCharts(
                    exchangeType = exchangeType,
                    keyPairId = keyPairId,
                    coinType = coinType,
                    candleUnits = candleUnits,
                )

                // 거래 정보 조회
                val tradeInfo = autoTradeInfoService.makeCoinTradeInfo(
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
                strategyType = coinStrategyType
            )

            // 작업 실행
            coinAutoTradeTaskService.execute(
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
}