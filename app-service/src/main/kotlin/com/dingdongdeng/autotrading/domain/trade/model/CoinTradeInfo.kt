package com.dingdongdeng.autotrading.domain.trade.model

import com.dingdongdeng.autotrading.domain.trade.entity.CoinTradeHistory
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext

data class CoinTradeInfo(
    val volume: Double,     // 수량        ex) 이더리움 1.38개 보유
    val averagePrice: Double,  // 평균 단가    ex) 이더리움 평균 단가 807,302원
    val valuePrice: Double,   // 현재 평가 금액     ex) 이더리움 평가 가치가 1,305,783원
    val originPrice: Double, // 매수했던 시점의 평가 금액
    val profitPrice: Double,   // 손익 평가 금액 ex) valuePrice - originPrice
    val coinTradeHistory: List<CoinTradeHistory>,
) {
    fun existsWaitTrade(): Boolean = coinTradeHistory.any { it.isWait() }

    fun getOldWaitTrades(seconds: Long): List<CoinTradeHistory> { //FIXME 도메인 로직 이동좀
        return coinTradeHistory.filter {
            // 대기 상태이면서 N초 이상 지난 거래들
            it.isWait() && it.tradedAt < TimeContext.now().minusSeconds(seconds)
        }
    }
}