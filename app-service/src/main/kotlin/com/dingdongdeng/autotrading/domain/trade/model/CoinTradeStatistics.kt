package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDate

data class CoinTradeStatistics(
    val coinType: CoinType,
    val from: LocalDate,
    val to: LocalDate,
    val processorId: String,
    val tradeHistories: List<CoinTradeHistory>,
) {
    val totalAccProfitPrice = tradeHistories.sumOf { it.profit }
}