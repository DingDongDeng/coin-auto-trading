package com.dingdongdeng.autotrading.domain.strategy.model

import com.dingdongdeng.autotrading.domain.indicator.model.Indicators
import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
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
    val currentPrice: Double, // 어느 차트를 봐도 이 값은 동일
    val candleUnit: CandleUnit,
    val candles: List<SpotCoinStrategyChartCandleParam>,
)

data class SpotCoinStrategyChartCandleParam(
    val candleUnit: CandleUnit,
    val candleDateTimeUtc: LocalDateTime, // 캔들 기준 시각(UTC 기준)
    val candleDateTimeKst: LocalDateTime, // 캔들 기준 시각(KST 기준)
    val openingPrice: Double, // 시가
    val highPrice: Double, // 고가
    val lowPrice: Double, // 저가
    val closingPrice: Double, // 종가
    val accTradePrice: Double, // 누적 거래 금액
    val accTradeVolume: Double, // 누적 거래량
    val indicators: Indicators, // 보조 지표
)

data class SpotCoinStrategyTradeInfoParam(
    val volume: Double,     // 수량        ex) 이더리움 1.38개 보유
    val averagePrice: Double,  // 평균 단가    ex) 이더리움 평균 단가 807,302원
    val valuePrice: Double,   // 현재 평가 금액     ex) 이더리움 평가 가치가 1,305,783원
    val originPrice: Double, // 매수했던 시점의 평가 금액
    val profitPrice: Double,   // 손익 평가 금액 ex) valuePrice - originPrice
    val coinTradeHistory: List<CoinTradeHistory>,
) {
    fun existsWaitTrade(): Boolean = coinTradeHistory.any { it.state == TradeState.WAIT }

    fun getOldWaitTrades(seconds: Long): List<CoinTradeHistory> {
        return coinTradeHistory.filter {
            // 대기 상태이면서 N초 이상 지난 거래들
            it.state == TradeState.WAIT && it.tradedAt.isBefore(TimeContext.now().minusSeconds(seconds))
        }
    }
}