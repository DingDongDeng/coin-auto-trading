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
    val monthlyStats: Map<CoinType, List<CoinBackTestStatistics>>,
    val tradeHistories: Map<CoinType, List<CoinBackTestTradeHistory>>,
)

data class CoinBackTestStatistics(
    // 기간별 통계
    val coinType: CoinType,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val profitRate: Double, // 수익율
    val profitPrice: Double, // 수익금
)

data class CoinBackTestTradeHistory(
    val coinType: CoinType,
    val orderType: OrderType,
    val volume: Double,
    val price: Double,
    val tradeAt: LocalDateTime,
)