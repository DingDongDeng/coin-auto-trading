package com.dingdongdeng.autotrading.usecase.autotrade

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyChartParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.service.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.TradeHistoryService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoinAutoTradeService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val strategyServices: List<SpotCoinStrategy>,
    private val autoTradeService: AutoTradeService,
    private val tradeHistoryService: TradeHistoryService,
) {

    fun register(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>, //TODO 캔들유닛타입별로 periodSize 필요 (from, to 범위 크기)
        keyPairId: String,
    ) {
        val strategyService = strategyServices.first { it.support(coinStrategyType) }
        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val keyParam = exchangeService.getExchangeKeyPair(keyPairId)


        LocalDateTime.now().minusMinutes()
        val process = {
            val makeTaskParams = coinTypes.map { coinType ->
                SpotCoinStrategyMakeTaskParam(
                    exchangeType = exchangeType,
                    coinType = coinType,
                    charts = candleUnits.map { candleUnit ->
                        val chartParam = SpotCoinExchangeChartParam(
                            coinType = coinType,
                            candleUnit = candleUnit,
                            from = LocalDateTime.now(),
                            to = LocalDateTime.now(),
                        )
                        val charts = exchangeService.getChart(chartParam, keyParam)

                        SpotCoinStrategyChartParam(

                        )
                    },
                    tradeInfo =
                )
            }
            strategyService.makeTask(makeTaskParams)
            strategyService.handleResult()
        }

        val autoTradeProcessor = autoTradeService.register(userId, process, 10000)
    }

    private fun makeStrategyMakeParam(coinType: CoinType) {

    }
}