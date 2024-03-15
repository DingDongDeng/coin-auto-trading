package com.dingdongdeng.autotrading.domain.trade.model

data class CoinTradeResultDetail(
    val summary: CoinTradeSummary,
    val statistics: List<CoinTradeStatistics>,
)

