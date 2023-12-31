package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderState
import org.springframework.data.jpa.repository.JpaRepository

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    fun findByAutoTradeProcessorIdAndCoinTypeAndStateInOrderByTradedAtAsc(
        autoTradeProcessorId: String,
        coinType: CoinType,
        states: List<OrderState>,
    ): List<CoinTradeHistory>
}