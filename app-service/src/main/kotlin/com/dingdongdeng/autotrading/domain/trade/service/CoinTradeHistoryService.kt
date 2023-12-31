package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.domain.trade.type.TradeStatus
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.stereotype.Service

@Service
class CoinTradeHistoryService(
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun findAllTradeHistory(
        autoTradeProcessorId: String,
        coinType: CoinType,
        tradeStatus: List<TradeStatus> = listOf(
            TradeStatus.WAIT,
            TradeStatus.DONE,
            TradeStatus.CANCEL
        )
    ): List<CoinTradeHistory> {
        return coinTradeHistoryRepository.findByAutoTradeProcessorIdAndCoinTypeAndStatusInOrderByTradedAtAsc(
            autoTradeProcessorId,
            coinType,
            tradeStatus,
        )
    }

    fun save(history: CoinTradeHistory): CoinTradeHistory {
        return coinTradeHistoryRepository.save(history)
    }
}