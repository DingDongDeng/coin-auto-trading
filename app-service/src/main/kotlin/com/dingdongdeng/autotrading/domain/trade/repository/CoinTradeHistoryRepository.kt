package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.type.TradeStatus
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.data.jpa.repository.JpaRepository

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    fun findByAutoTradeProcessorIdAndCoinTypeAndStatusInOrderByTradedAtAsc(
        autoTradeProcessorId: String,
        coinType: CoinType,
        status: List<TradeStatus>,
    ): List<CoinTradeHistory>
}