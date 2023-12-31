package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.domain.indicator.model.Indicators
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.time.LocalDateTime

data class SpotCoinStrategyMakeTaskParam(
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val charts: List<SpotCoinStrategyChartParam>, // 1분봉, 5분봉, 60분봉 등
    val tradeInfo: SpotCoinStrategyTradeInfoParam,
) {
    fun getChart(candleUnit: CandleUnit): SpotCoinStrategyChartParam = charts.first { it.candleUnit == candleUnit }
}

data class SpotCoinStrategyChartParam(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val currentPrice: Int,
    val candleUnit: CandleUnit,
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
    val volume: Double,     // 수량        ex) 이더리움 1.38개 보유
    val averagePrice: Int,  // 평균 단가    ex) 이더리움 평균 단가 807,302원
    val valuePrice: Int,   // 현재 평가 금액     ex) 이더리움 평가 가치가 1,305,783원
    val originPrice: Int, // 매수했던 시점의 평가 금액
    val profitPrice: Int,   // 손익 평가 금액 ex) valuePrice - originPrice
    val coinTradeHistory: List<CoinTradeHistory>,
)