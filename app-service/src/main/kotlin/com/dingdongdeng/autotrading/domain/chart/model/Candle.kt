package com.dingdongdeng.autotrading.domain.chart.model

import com.dingdongdeng.autotrading.domain.indicator.model.Indicators
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import java.time.LocalDateTime

data class Candle(
    val candleUnit: CandleUnit,
    val candleDateTimeUtc: LocalDateTime, // 캔들 기준 시각(UTC 기준)
    val candleDateTimeKst: LocalDateTime, // 캔들 기준 시각(KST 기준)
    val openingPrice: Double, // 시가
    val highPrice: Double, // 고가
    val lowPrice: Double, // 저가
    val closingPrice: Double, // 종가
    val accTradePrice: Double, // 누적 거래 금액
    val accTradeVolume: Double, // 누적 거래량
    private val indicatorsFunc: () -> Indicators, // 보조지표 계산
) {
    private val indicators by lazy(indicatorsFunc)

    fun getIndicators(): Indicators = indicators
}