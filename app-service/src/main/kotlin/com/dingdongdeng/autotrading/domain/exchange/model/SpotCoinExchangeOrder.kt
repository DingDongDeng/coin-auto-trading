package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import java.time.LocalDateTime

data class SpotCoinExchangeOrder(
    val orderId: String, // 주문의 고유 아이디
    val orderType: OrderType, // 주문 종류
    val priceType: PriceType, // 주문 방식
    val price: Double, // 주문 당시 화폐 가격
    val volume: Double,
    val tradeState: TradeState, // 주문 상태
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val fee: Double,
    val orderDateTime: LocalDateTime? = null, // 주문 시간
    val cancelDateTime: LocalDateTime? = null, // 취소 시간
)

