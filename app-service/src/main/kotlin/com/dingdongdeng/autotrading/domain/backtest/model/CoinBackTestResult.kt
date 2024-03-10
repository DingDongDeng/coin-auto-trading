package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import java.time.LocalDateTime

data class CoinBackTestResult(
    val backTestProcessorId: String,
    val progressRate: Double,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val totalProfitRate: Double,
    val totalProfitPrice: Double,
    val totalFee: Double,
    val tradeHistories: Map<CoinType, List<CoinBackTestTradeHistory>>,
)

data class CoinBackTestTradeHistory(
    val coinType: CoinType,
    val orderType: OrderType,
    val volume: Double,
    val price: Double,
    val tradeAt: LocalDateTime,
)