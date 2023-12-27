package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.type.CoinExchangeType
import com.dingdongdeng.autotrading.type.CoinType
import com.dingdongdeng.autotrading.type.OrderState
import com.dingdongdeng.autotrading.type.OrderType
import com.dingdongdeng.autotrading.type.PriceType
import java.time.LocalDateTime

data class SpotCoinExchangeOrderResult(
    val orderId: String, // 주문의 고유 아이디
    val orderType: OrderType, // 주문 종류
    val priceType: PriceType, // 주문 방식
    val price: Double, // 주문 당시 화폐 가격
    val orderState: OrderState, // 주문 상태
    val coinExchangeType: CoinExchangeType,
    val coinType: CoinType,
    val createdAt: LocalDateTime, // 주문 생성 시간
)

