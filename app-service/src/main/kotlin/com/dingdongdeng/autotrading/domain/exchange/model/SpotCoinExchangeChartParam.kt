package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class SpotCoinExchangeChartParam(
    val coinType: CoinType,
    val candleUnit: CandleUnit,
    val from: LocalDateTime,
    val to: LocalDateTime,
)
