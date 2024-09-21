package com.dingdongdeng.autotrading.application.backtest.model

import com.dingdongdeng.autotrading.domain.chart.model.Candle
import com.dingdongdeng.autotrading.domain.chart.model.Chart
import com.dingdongdeng.autotrading.domain.trade.model.CoinTradeSummary
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import java.time.LocalDateTime

data class CoinBackTestReplayDto(
    val backTestProcessorId: String,
    val replayDateTime: LocalDateTime,
    val next: Boolean,
    val charts: List<CoinBackTestReplayChartDto>,
)

data class CoinBackTestReplayChartDto(
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val candleUnit: CandleUnit,
    val candles: List<CoinBackTestReplayChartCandleDto>,
    val trades: List<CoinBackTestReplayTradeDto>,
) {
    companion object {
        fun of(
            replayDateTime: LocalDateTime,
            exchangeType: ExchangeType,
            coinType: CoinType,
            chart: Chart,
            tradeSummary: CoinTradeSummary,
        ): CoinBackTestReplayChartDto {
            return CoinBackTestReplayChartDto(
                exchangeType = exchangeType,
                coinType = coinType,
                candleUnit = chart.candleUnit,
                candles = chart.candles
                    .filter { it.candleDateTimeKst > replayDateTime }
                    .map { CoinBackTestReplayChartCandleDto.of(it) },
                trades = tradeSummary.tradeHistories
                    .filter { it.tradedAt > replayDateTime }
                    .map {
                        CoinBackTestReplayTradeDto(
                            orderType = it.orderType,
                            price = it.price,
                            timestamp = it.tradedAt,
                        )
                    }
            )
        }
    }
}

data class CoinBackTestReplayChartCandleDto(
    val openingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closingPrice: Double,
    val timestamp: Long,
) {
    companion object {
        fun of(candle: Candle): CoinBackTestReplayChartCandleDto {
            return CoinBackTestReplayChartCandleDto(
                openingPrice = candle.openingPrice,
                highPrice = candle.highPrice,
                lowPrice = candle.lowPrice,
                closingPrice = candle.closingPrice,
                timestamp = candle.candleDateTimeKst,
            )
        }
    }
}

data class CoinBackTestReplayTradeDto(
    val orderType: OrderType,
    val price: Double,
    val timestamp: Long,
)