package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

data class SpotCoinStrategyMakeTaskParam(
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val charts: List<Chart>, // 1분봉, 5분봉, 60분봉 등
    val tradeSummary: CoinTradeSummary,
) {
    fun getChart(candleUnit: CandleUnit): Chart = charts.first { it.candleUnit == candleUnit }
}