package com.dingdongdeng.autotrading.application.autotrade.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import java.time.LocalDate
import java.time.LocalDateTime

data class CoinAutoTradeResultDto(
    val autoTradeProcessorId: String,
    val totalProfitRate: Double,
    val totalProfitPrice: Double,
    val totalAccProfitValuePrice: Double, // 수수료 제외되어있음
    val totalFee: Double,
    val tradeHistoriesMap: Map<CoinType, List<CoinAutoTradeHistory>>,
    val tradeStatisticsMap: Map<CoinType, List<CoinAutoTradeStatistics>>
)

data class CoinAutoTradeHistory(
    val coinType: CoinType,
    val orderType: OrderType,
    val volume: Double,
    val price: Double,
    val profit: Double,
    val tradeAt: LocalDateTime,
)

data class CoinAutoTradeStatistics(
    val coinType: CoinType,
    val from: LocalDate,
    val to: LocalDate,
    val totalAccProfitPrice: Double,
)