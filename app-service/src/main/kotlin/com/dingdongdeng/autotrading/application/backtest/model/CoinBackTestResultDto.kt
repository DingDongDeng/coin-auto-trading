package com.dingdongdeng.autotrading.application.backtest.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import java.time.LocalDate
import java.time.LocalDateTime

data class CoinBackTestResultDto(
    val backTestProcessorId: String,
    val progressRate: Double,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val totalProfitRate: Double,
    val totalProfitPrice: Double,
    val totalAccProfitValuePrice: Double, // 수수료 제외되어있음
    val totalFee: Double,
    val tradeHistories: Map<CoinType, List<CoinBackTestTradeHistory>>,
    val tradeStatistics: Map<CoinType, List<CoinBackTestTradeStatistics>>
)

data class CoinBackTestTradeHistory(
    val coinType: CoinType,
    val orderType: OrderType,
    val volume: Double,
    val price: Double,
    val profit: Double,
    val tradeAt: LocalDateTime,
)

data class CoinBackTestTradeStatistics(
    val coinType: CoinType,
    val from: LocalDate,
    val to: LocalDate,
    val totalAccProfitPrice: Double,
)