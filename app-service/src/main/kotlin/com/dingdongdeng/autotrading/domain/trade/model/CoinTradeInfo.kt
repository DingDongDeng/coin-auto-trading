package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory

data class CoinTradeInfo(
    val volume: Double,     // 수량        ex) 이더리움 1.38개 보유
    val averagePrice: Double,  // 평균 단가    ex) 이더리움 평균 단가 807,302원
    val valuePrice: Double,   // 현재 평가 금액     ex) 이더리움 평가 가치가 1,305,783원
    val originPrice: Double, // 매수했던 시점의 평가 금액
    val profitPrice: Double,   // 손익 평가 금액 ex) valuePrice - originPrice
    val tradeHistory: List<CoinTradeHistory>,
) {
    val existsWaitTrade = tradeHistory.any { it.isWait() }
    val buyTradeHistory = tradeHistory.filter { it.isBuyOrder() }

    fun getOldWaitTrades(seconds: Long): List<CoinTradeHistory> {
        return tradeHistory.filter { it.isOldWait(seconds) }
    }
}