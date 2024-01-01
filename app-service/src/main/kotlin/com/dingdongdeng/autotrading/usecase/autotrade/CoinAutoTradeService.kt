package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.service.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CoinAutoTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val strategyServices: List<SpotCoinStrategy>,
    private val autoTradeService: AutoTradeService,

    private val coinAutoTradeChartService: CoinAutoTradeChartService,
    private val coinAutoTradeInfoService: CoinAutoTradeInfoService,
    private val coinAutoTradeOrderService: CoinAutoTradeOrderService,
) {

    fun register(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        //TODO strategy에서 사용할 커스텀 파라미터도 추가 필요해
    ): String {

        val autoTradeProcessorId = UUID.randomUUID().toString()
        val strategyService = strategyServices.first { it.support(coinStrategyType) }
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getExchangeKeyPair(keyPairId)

        val process = {
            val makeTaskParams = coinTypes.map { coinType ->
                // 차트 조회
                val charts = coinAutoTradeChartService.makeCharts(
                    exchangeType = exchangeType,
                    exchangeKeyPair = exchangeKeyPair,
                    coinType = coinType,
                    candleUnits = candleUnits,
                )

                // 거래 정보 조회
                val tradeInfo = coinAutoTradeInfoService.makeTradeInfo(
                    exchangeType = exchangeType,
                    exchangeKeyPair = exchangeKeyPair,
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
            strategyService.makeTask(makeTaskParams).forEach { task ->
                coinAutoTradeOrderService.order(
                    exchangeType = exchangeType,
                    exchangeKeyPair = exchangeKeyPair,
                    autoTradeProcessorId = autoTradeProcessorId,
                    task = task,
                )
            }
        }

        return autoTradeService.register(autoTradeProcessorId, userId, process, 10000)
    }
}