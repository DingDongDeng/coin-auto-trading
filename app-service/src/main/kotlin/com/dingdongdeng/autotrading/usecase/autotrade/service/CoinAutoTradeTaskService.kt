package com.dingdongdeng.autotrading.usecase.autotrade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.service.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeHistoryService
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import org.springframework.stereotype.Service

@Service
class CoinAutoTradeTaskService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val strategyServices: List<SpotCoinStrategy>,
    private val coinTradeHistoryService: CoinTradeHistoryService,
) {

    fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>,
        strategyType: CoinStrategyType
    ): List<SpotCoinStrategyTask> {
        val strategyService = strategyServices.first { it.support(strategyType) }
        return strategyService.makeTask(params, config)
    }

    fun executeTask(
        tasks: List<SpotCoinStrategyTask>,
        autoTradeProcessorId: String,
        keyPairId: String,
        exchangeType: ExchangeType,
    ) {
        tasks.forEach { task ->
            val exchangeService = exchangeServices.first { it.support(exchangeType) }
            val exchangeKeyPair = exchangeService.getKeyPair(keyPairId)
            val orderResponse = when (task.orderType) {
                OrderType.BUY, OrderType.SELL -> {
                    val param = SpotCoinExchangeOrderParam(
                        coinType = task.coinType,
                        orderType = task.orderType,
                        volume = task.volume,
                        price = task.price,
                        priceType = task.priceType,
                    )
                    exchangeService.order(param, exchangeKeyPair)
                }

                OrderType.CANCEL -> exchangeService.cancel(task.orderId!!, exchangeKeyPair)
            }

            // 취소 상태 업데이트
            if (orderResponse.orderType == OrderType.CANCEL) {
                coinTradeHistoryService.cancel(orderResponse.orderId)
                return
            }

            // 매수, 매도 기록
            coinTradeHistoryService.record(
                makeTradeHistory(
                    order = orderResponse,
                    autoTradeProcessorId = autoTradeProcessorId
                )
            )
        }
    }

    private fun makeTradeHistory(
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String
    ): CoinTradeHistory {
        return CoinTradeHistory(
            orderId = order.orderId,
            state = order.tradeState,
            processorId = autoTradeProcessorId,
            exchangeType = order.exchangeType,
            coinType = order.coinType,
            orderType = order.orderType,
            priceType = order.priceType,
            volume = order.volume,
            price = order.price,
            fee = order.fee,
            tradedAt = order.orderDateTime!!,
        )
    }
}