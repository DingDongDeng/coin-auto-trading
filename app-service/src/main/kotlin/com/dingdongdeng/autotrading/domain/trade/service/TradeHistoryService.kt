package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.trade.entity.TradeHistory
import com.dingdongdeng.autotrading.domain.trade.repository.TradeHistoryRepository
import org.springframework.stereotype.Service

@Service
class TradeHistoryService(
    private val tradeHistoryRepository: TradeHistoryRepository,
) {

    fun findAllAutoTradeProcessorTradeHistory(autoTradeProcessorId: String): List<TradeHistory> {
        return tradeHistoryRepository.findByAutoTradeProcessorIdOrderByTradedAtAsc(autoTradeProcessorId)
    }
}