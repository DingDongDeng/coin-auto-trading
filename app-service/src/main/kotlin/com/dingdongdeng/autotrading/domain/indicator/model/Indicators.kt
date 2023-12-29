package com.dingdongdeng.autotrading.domain.indicator.model

import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class Indicators(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val indicatorDateTime: LocalDateTime,
    val rsi: Double,
    val macd: Macd,
    val bollingerBands: BollingerBands,
    val obv: Obv,
    val ma: Ma,
)
