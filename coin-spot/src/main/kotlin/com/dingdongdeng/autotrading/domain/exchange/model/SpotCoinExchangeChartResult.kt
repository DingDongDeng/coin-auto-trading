package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.type.CoinExchangeType
import com.dingdongdeng.autotrading.type.CoinType
import java.time.LocalDateTime

data class SpotCoinExchangeChartResult(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Int,
    val candles: List<SpotCoinExchangeCandleResult>,
    val indicators: List<SpotCoinExchangeIndicatorResult>,
)

data class SpotCoinExchangeCandleResult(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val from: LocalDateTime,
    val to: LocalDateTime,
)

data class SpotCoinExchangeIndicatorResult(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val dateTime: LocalDateTime,
    val rsi: Double,
)
