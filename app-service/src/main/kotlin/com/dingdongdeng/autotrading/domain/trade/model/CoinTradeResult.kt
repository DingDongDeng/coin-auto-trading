package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.infra.common.utils.round
import java.time.LocalDateTime

data class CoinTradeResult(
    val now: LocalDateTime,
    val processorId: String,
    val details: List<CoinTradeResultDetail>,
) {
    val totalCurrentValuePrice = details.map { it.summary }.sumOf { it.currentValuePrice }
    val totalAverageValuePrice = details.map { it.summary }.sumOf { it.averageValuePrice }
    val totalAccProfitPrice = details.map { it.summary }.sumOf { it.accProfitPrice }
    val totalProfitPrice = totalCurrentValuePrice - totalAverageValuePrice
    val totalProfitRate =
        if (totalAverageValuePrice == 0.0) 0.0 else ((totalProfitPrice / totalAverageValuePrice) * 100.0).round(2.0)// 수익율 (xx.xx%)
    val totalFee = details.map { it.summary }.sumOf { it.fee }
}
