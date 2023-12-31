package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import org.springframework.stereotype.Service

@Service
class TradeHistoryService(
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun findAllAutoTradeProcessorTradeHistory(autoTradeProcessorId: String): List<CoinTradeHistory> {
        return coinTradeHistoryRepository.findByAutoTradeProcessorIdOrderByTradedAtAsc(autoTradeProcessorId)
    }
}