package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import org.springframework.data.jpa.repository.JpaRepository

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    fun findByAutoTradeProcessorIdOrderByTradedAtAsc(autoTradeProcessorId: String): List<CoinTradeHistory>
}