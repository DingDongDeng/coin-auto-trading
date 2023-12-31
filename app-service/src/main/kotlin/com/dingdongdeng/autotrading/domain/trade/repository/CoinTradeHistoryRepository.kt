package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.data.jpa.repository.JpaRepository

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    fun findByAutoTradeProcessorIdAndCoinTypeOrderByTradedAtAsc(
        autoTradeProcessorId: String,
        coinType: CoinType
    ): List<CoinTradeHistory>
}