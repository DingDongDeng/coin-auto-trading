package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import org.springframework.data.jpa.repository.JpaRepository

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    fun findByProcessorIdAndCoinTypeAndStateInOrderByTradedAtAsc(
        autoTradeProcessorId: String,
        coinType: CoinType,
        states: List<TradeState>,
    ): List<CoinTradeHistory>

    fun findByOrderId(orderId: String): CoinTradeHistory
}