package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.utils.round
import java.time.LocalDateTime

data class CoinTradeInfo(
    val currentPrice: Double,
    val tradeHistories: List<CoinTradeHistory>,
) {
    val buyTradeHistories = tradeHistories.filter { it.isBuyOrder() }
    val sellTradeHistories = tradeHistories.filter { it.isSellOrder() }

    val volume = buyTradeHistories.sumOf { it.volume } - sellTradeHistories.sumOf { it.volume }

    val averagePrice = if (volume == 0.0) 0.0 else (calcAveragePrice()).round()
    val currentValuePrice = (volume * currentPrice).round()   // 현재 평가 금액   ex) 보유수량 X 현재가
    val averageValuePrice = (volume * averagePrice).round() // 평단가 평가 금액   ex) 보유수량 X 평단가
    val profitPrice = (currentValuePrice - averageValuePrice) // 손익 평가 금액 ex) currentValuePrice - averageValuePrice
    val profitRate =
        if (averageValuePrice == 0.0) 0.0 else ((profitPrice / averageValuePrice) * 100.0).round(2.0)// 수익율 (xx.xx%)

    val existsWaitTrade = tradeHistories.any { it.isWait() }
    val hasVolume = volume != 0.0
    val lastTradedAt = if (tradeHistories.isEmpty()) LocalDateTime.MIN else tradeHistories.last().tradedAt

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