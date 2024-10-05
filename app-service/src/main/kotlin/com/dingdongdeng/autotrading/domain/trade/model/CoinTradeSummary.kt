package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDateTime

data class CoinTradeSummary(
    val coinType: CoinType,
    val now: LocalDateTime,
    val processorId: String,
    val currentPrice: Double,
    val tradeHistories: List<CoinTradeHistory>,
) {
    val buyTradeHistories = tradeHistories.filter { it.isBuyOrder() }
    val sellTradeHistories = tradeHistories.filter { it.isSellOrder() }

    val volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume }
    val fee = tradeHistories.sumOf { it.fee }

    val buyValuePrice = buyTradeHistories.sumOf { it.volume * it.price }
    val sellValuePrice = sellTradeHistories.sumOf { it.volume * it.price }
    val averagePrice = if (volume == 0.0) 0.0 else (calcAveragePrice())
    val currentValuePrice = (volume * currentPrice)   // 현재 평가 금액   ex) 보유수량 X 현재가
    val averageValuePrice = (volume * averagePrice) // 평단가 평가 금액   ex) 보유수량 X 평단가

    val accProfitPrice = (sellTradeHistories.sumOf { it.profit }) // 누적 이익금
    val profitPrice = (currentValuePrice - averageValuePrice) // 손익 평가 금액 ex) currentValuePrice - averageValuePrice
    val profitRate =
        if (averageValuePrice == 0.0) 0.0 else ((profitPrice / averageValuePrice) * 100.0)// 수익율 (xx.xx%)


    val existsWaitTrade = tradeHistories.any { it.isWait() }
    val hasVolume = volume != 0.0
    val lastTradedAt = if (tradeHistories.isEmpty()) LocalDateTime.MIN else tradeHistories.last().tradedAt
    val lastBuyTradedAt = if (buyTradeHistories.isEmpty()) LocalDateTime.MIN else buyTradeHistories.last().tradedAt
    val lastSellTradedAt = if (sellTradeHistories.isEmpty()) LocalDateTime.MIN else sellTradeHistories.last().tradedAt

    fun getOldWaitTrades(seconds: Long): List<CoinTradeHistory> {
        return tradeHistories.filter { it.isOldWait(seconds) }
    }

    private fun calcAveragePrice(): Double {
        var value = 0.0
        var volume = 0.0
        for (tradeHistory in tradeHistories) {
            if (tradeHistory.isBuyOrder()) {
                value += tradeHistory.price * tradeHistory.volume
                volume += tradeHistory.volume
            }
            if (tradeHistory.isSellOrder()) {
                value -= (value / volume) * tradeHistory.volume
                volume -= tradeHistory.volume
            }
        }

        return value / volume
    }
}