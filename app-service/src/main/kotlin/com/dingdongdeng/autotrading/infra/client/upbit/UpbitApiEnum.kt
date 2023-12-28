package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderState
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType

enum class State(
    val desc: String,
    val orderState: OrderState
) {
    wait("체결 대기", OrderState.WAIT),
    watch("예약주문 대기", OrderState.WATCH),
    done("전체 체결 완료", OrderState.DONE),
    cancel("주문 취소", OrderState.CANCEL),
    ;
}

enum class MarketType(
    val desc: String,
    val code: String,
    val coinType: CoinType,
) {
    KRW_ADA("원화 에이다", "KRW-ADA", CoinType.ADA),
    KRW_SOL("원화 솔라나", "KRW-SOL", CoinType.SOLANA),
    KRW_XRP("원화 리플", "KRW-XRP", CoinType.XRP),
    KRW_ETH("원화 이더리움", "KRW-ETH", CoinType.ETHEREUM),
    KRW_AVAX("원화 아발란체", "KRW-AVAX", CoinType.AVALANCHE),
    KRW_DOT("원화 폴카닷", "KRW-DOT", CoinType.POLKADOT),
    KRW_DOGE("원화 도지", "KRW-DOGE", CoinType.DOGE),
    KRW_BTC("원화 비트코", "KRW-BTC", CoinType.BITCOIN),
    ;


    companion object {
        fun of(coinType: CoinType): MarketType {
            return values().first { it.coinType === coinType }
        }

        fun of(code: String): MarketType {
            return values().first { it.code == code }
        }
    }
}

enum class OrdType(
    val desc: String,
    val priceType: PriceType,
    val orderTypeList: List<OrderType>,
) {
    limit("지정가 주문", PriceType.LIMIT, listOf(OrderType.BUY, OrderType.SELL)),
    price("시장가 주문(매수)", PriceType.MARKET, listOf(OrderType.BUY)),
    market("시장가 주문(매도)", PriceType.MARKET, listOf(OrderType.SELL));

    companion object {
        fun of(priceType: PriceType, orderType: OrderType?): OrdType {
            return values().first { it.priceType == priceType && it.orderTypeList.contains(orderType) }
        }
    }
}


enum class Side(
    val desc: String,
    val orderType: OrderType,
) {
    bid("매수", OrderType.BUY),
    ask("매도", OrderType.SELL),
    ;

    companion object {
        fun of(orderType: OrderType): Side {
            return values().first { it.orderType == orderType }
        }
    }
}

