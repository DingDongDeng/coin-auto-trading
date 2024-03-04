package com.dingdongdeng.autotrading.domain.trade.repository

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CoinTradeHistoryRepository : JpaRepository<CoinTradeHistory, Long> {

    @Query(
        """
        select history 
        from CoinTradeHistory history
        where history.processorId = :processorId
        and history.coinType = :coinType
        order by history.tradedAt
    """
    )
    fun findAllCoinTradeHistories(
        processorId: String,
        coinType: CoinType,
    ): List<CoinTradeHistory>

    fun findByOrderId(orderId: String): CoinTradeHistory
}