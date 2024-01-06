package com.dingdongdeng.autotrading.usecase.autotrade.service

import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeHistoryService
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import org.springframework.stereotype.Service

@Service
class CoinAutoTradeOrderService(
    private val exchangeServices: List<SpotCoinExchangeService>,
    private val coinTradeHistoryService: CoinTradeHistoryService,
) {
    fun order(
        exchangeType: ExchangeType,
        keyPairId: String,
        autoTradeProcessorId: String,
        task: SpotCoinStrategyTask,
    ) {

        val exchangeService = exchangeServices.first { it.support(exchangeType) }
        val exchangeKeyPair = exchangeService.getExchangeKeyPair(keyPairId)
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

        coinTradeHistoryService.save(
            makeCoinTradeHistory(
                order = orderResponse,
                autoTradeProcessorId = autoTradeProcessorId
            )
        )
    }

    private fun makeCoinTradeHistory(
        coinTradehistoryId: Long? = null,
        order: SpotCoinExchangeOrder,
        autoTradeProcessorId: String
    ): CoinTradeHistory {
        return CoinTradeHistory(
            id = coinTradehistoryId,
            orderId = order.orderId,
            state = order.tradeState,
            autoTradeProcessorId = autoTradeProcessorId,
            exchangeType = order.exchangeType,
            coinType = order.coinType,
            orderType = order.orderType,
            priceType = order.priceType,
            volume = order.volume,
            price = order.price,
            fee = order.fee,
            tradedAt = if (order.orderType == OrderType.CANCEL) order.cancelDateTime!! else order.orderDateTime!!,
        )
    }
}