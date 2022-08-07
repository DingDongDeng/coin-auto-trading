package com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UpbitEnum {

    @Getter
    @AllArgsConstructor
    public enum State {

        wait("체결 대기", OrderState.WAIT),
        watch("예약주문 대기", OrderState.WATCH),
        done("전체 체결 완료", OrderState.DONE),
        cancel("주문 취소", OrderState.CANCEL);

        private String desc;
        private OrderState orderState;

    }

    @Getter
    @AllArgsConstructor
    public enum MarketType {
        KRW_ADA("원화 에이다", "KRW-ADA", CoinType.ADA),
        KRW_SOL("원화 솔라나", "KRW-SOL", CoinType.SOLANA),
        KRW_XRP("원화 리플", "KRW-XRP", CoinType.XRP),
        KRW_ETH("원화 이더리움", "KRW-ETH", CoinType.ETHEREUM),
        KRW_AVAX("원화 아발란체", "KRW-AVAX", CoinType.AVALANCHE),
        KRW_DOT("원화 폴카닷", "KRW-DOT", CoinType.POLKADOT),
        KRW_DOGE("원화 도지", "KRW-DOGE", CoinType.DOGE),
        KRW_BTC("원화 비트코", "KRW-BTC", CoinType.BITCOIN),

        ;
        private String desc;
        private String code;
        private CoinType coinType;

        public static MarketType of(CoinType coinType) {
            return Arrays.stream(MarketType.values())
                .filter(type -> type.getCoinType() == coinType)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(coinType.name()));
        }

        public static MarketType of(String code) {
            return Arrays.stream(MarketType.values())
                .filter(type -> type.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(code));

        }
    }

    @Getter
    @AllArgsConstructor
    public enum OrdType {
        limit("지정가 주문", PriceType.LIMIT, List.of(OrderType.BUY, OrderType.SELL)),
        price("시장가 주문(매수)", PriceType.MARKET, List.of(OrderType.BUY)),
        market("시장가 주문(매도)", PriceType.MARKET, List.of(OrderType.SELL)),
        ;
        private String desc;
        private PriceType priceType;
        private List<OrderType> orderTypeList;

        public static OrdType of(PriceType priceType, OrderType orderType) {
            return Arrays.stream(OrdType.values())
                .filter(type -> type.getPriceType() == priceType)
                .filter(type -> type.getOrderTypeList().contains(orderType))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(priceType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Side {
        bid("매수", OrderType.BUY),
        ask("매도", OrderType.SELL),
        ;

        private String desc;
        private OrderType orderType;

        public static Side of(OrderType orderType) {
            return Arrays.stream(Side.values())
                .filter(type -> type.getOrderType() == orderType)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(orderType.name()));
        }
    }
}
