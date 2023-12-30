package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.domain.indicator.model.Indicators
import com.dingdongdeng.autotrading.domain.trade.entity.TradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class SpotCoinStrategyMakeTaskParam(
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val charts: List<SpotCoinStrategyChartParam>,
    val tradeInfo: List<SpotCoinStrategyTradeInfoParam>,
)

data class SpotCoinStrategyChartParam(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Int,
    val candles: List<SpotCoinStrategyChartCandleParam>,
)

data class SpotCoinStrategyChartCandleParam(
    val candleUnit: CandleUnit,
    val candleDateTimeUtc: LocalDateTime, // 캔들 기준 시각(UTC 기준)
    val candleDateTimeKst: LocalDateTime, // 캔들 기준 시각(KST 기준)
    val openingPrice: Int, // 시가
    val highPrice: Int, // 고가
    val lowPrice: Int, // 저가
    val closingPrice: Int, // 종가
    val accTradePrice: Double, // 누적 거래 금액
    val accTradeVolume: Double, // 누적 거래량
    val indicators: Indicators, // 보조 지표
)

data class SpotCoinStrategyTradeInfoParam(
    val volume: Double,
    val averagePrice: Int,
    val lossProfitPrice: Int, // 손익 금액
    val tradeHistory: List<TradeHistory>,
)