package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class SpotCoinExchangeChartResult(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Int,
    val candles: List<SpotCoinExchangeCandleResult>,
)

data class SpotCoinExchangeCandleResult(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val candleUnit: CandleUnit,
    val candleDateTimeUtc: LocalDateTime, // 캔들 기준 시각(UTC 기준)
    val candleDateTimeKst: LocalDateTime, // 캔들 기준 시각(KST 기준)
    val openingPrice: Int, // 시가
    val highPrice: Int, // 고가
    val lowPrice: Int, // 저가
    val closingPrice: Int, // 종가
    val accTradePrice: Double, // 누적 거래 금액
    val accTradeVolume: Double, // 누적 거래량
)