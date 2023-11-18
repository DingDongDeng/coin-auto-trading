package com.dingdongdeng.autotrading.upbit

import com.dingdongdeng.autotrading.type.CoinType
import com.dingdongdeng.autotrading.type.OrderState

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
            return values().first { it.code === code }
        }
    }
}
