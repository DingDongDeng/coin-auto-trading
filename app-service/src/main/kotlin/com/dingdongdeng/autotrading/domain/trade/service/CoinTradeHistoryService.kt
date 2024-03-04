package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.infra.common.annotation.DomainService
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.TradeState

@DomainService
class CoinTradeHistoryService(
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun findAllTradeHistory(
        autoTradeProcessorId: String,
        coinType: CoinType,
        states: List<TradeState> = listOf(
            TradeState.WAIT,
            TradeState.DONE,
            TradeState.CANCEL
        )
    ): List<CoinTradeHistory> {
        return coinTradeHistoryRepository.findAllCoinTradeHistories(
            autoTradeProcessorId,
            coinType,
            states,
        )
    }

    fun findTradeHistory(orderId: String): CoinTradeHistory {
        return coinTradeHistoryRepository.findByOrderId(orderId)
    }

    fun save(history: CoinTradeHistory): CoinTradeHistory {
        return coinTradeHistoryRepository.save(history)
    }
}