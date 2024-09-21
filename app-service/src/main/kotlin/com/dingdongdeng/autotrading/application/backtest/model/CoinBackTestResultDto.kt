package com.dingdongdeng.autotrading.application.backtest.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import java.time.LocalDate
import java.time.LocalDateTime

data class CoinBackTestResultDto( //FIXME durationUnit 있어야하지않음?
    val title: String,
    val strategyType: CoinStrategyType,
    val status: ProcessStatus,
    val backTestProcessorId: String,
    val config: Map<String, Any>,
    val progressRate: Double,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val totalProfitRate: Double,
    val totalProfitPrice: Double,
    val totalAccProfitValuePrice: Double, // 수수료 제외되어있음
    val totalFee: Double,
    val tradeHistoriesMap: Map<CoinType, List<CoinBackTestTradeHistory>>,
    val tradeStatisticsMap: Map<CoinType, List<CoinBackTestTradeStatistics>>
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