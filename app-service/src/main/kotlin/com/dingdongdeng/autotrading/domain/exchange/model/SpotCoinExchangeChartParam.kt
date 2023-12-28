package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class SpotCoinExchangeChartParam(
    val coinType: CoinType,
    val candleUnit: CandleUnit,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val chunkSize: Int = 200, // 한번에 조회하는 개수 (1000개를 조회하더라도 200개씩 나눠서 조회)
)
