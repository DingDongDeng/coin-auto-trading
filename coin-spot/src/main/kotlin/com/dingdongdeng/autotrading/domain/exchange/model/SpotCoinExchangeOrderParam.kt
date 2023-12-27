package com.dingdongdeng.autotrading.domain.exchange.model

import com.dingdongdeng.autotrading.type.CoinType
import com.dingdongdeng.autotrading.type.OrderType
import com.dingdongdeng.autotrading.type.PriceType

data class SpotCoinExchangeOrderParam(
    val coinType: CoinType, // 마켓 ID (필수)
    val orderType: OrderType, // 주문 종류 (필수) - bid : 매수 - ask : 매도
    val volume: Double, // 주문량 (지정가, 시장가 매도 시 필수)
    val price: Double, // 주문 가격. (지정가, 시장가 매수 시 필수)  ex) KRW-BTC 마켓에서 1BTC당 1,000 KRW로 거래할 경우, 값은 1000 이 된다. ex) KRW-BTC 마켓에서 1BTC당 매도 1호가가 500 KRW 인 경우, 시장가 매수 시 값을 1000으로 세팅하면 2BTC가 매수된다. (수수료가 존재하거나 매도 1호가의 수량에 따라 상이할 수 있음)
    val priceType: PriceType, // 주문 타입 (필수) - limit : 지정가 주문 - price : 시장가 주문(매수) - market : 시장가 주문(매도)
)
