package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.TradeHistory
import org.springframework.data.jpa.repository.JpaRepository

interface TradeHistoryRepository : JpaRepository<TradeHistory, Long> {

    fun findByAutoTradeProcessorIdOrderByTradedAtAsc(autoTradeProcessorId: String): List<TradeHistory>
}