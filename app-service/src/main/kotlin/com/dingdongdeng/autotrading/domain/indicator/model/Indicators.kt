package com.dingdongdeng.autotrading.domain.indicator.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.time.LocalDateTime

data class Indicators(
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val indicatorDateTime: LocalDateTime,
    val rsi: Double,
    val macd: Macd,
    val bollingerBands: BollingerBands,
    val obv: Obv,
    val ma: Ma,
)
