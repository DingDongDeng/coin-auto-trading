package com.dingdongdeng.autotrading.domain.trade.service

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.domain.trade.repository.CoinTradeHistoryRepository
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.stereotype.Service

@Service
class TradeHistoryService(
    private val coinTradeHistoryRepository: CoinTradeHistoryRepository,
) {

    fun findAllTradeHistory(autoTradeProcessorId: String, coinType: CoinType): List<CoinTradeHistory> {
        return coinTradeHistoryRepository.findByAutoTradeProcessorIdAndCoinTypeOrderByTradedAtAsc(
            autoTradeProcessorId,
            coinType
        )
    }
}