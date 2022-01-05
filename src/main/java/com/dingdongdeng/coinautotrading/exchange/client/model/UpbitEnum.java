package com.dingdongdeng.coinautotrading.exchange.client.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UpbitEnum {

    @Getter
    @AllArgsConstructor
    public enum State {

        wait("미체결", OrderState.WAIT),
        done("전체 체결 완료", OrderState.DONE),
        cancel("주문 취소", OrderState.CANCELED);

        private String desc;
        private OrderState orderState;

    }

    @Getter
    @AllArgsConstructor
    public enum MarketType {
        KRW_ETH("원화 이더리움", "KRW-ETH", CoinType.ETHEREUM),
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
    }

    @Getter
    @AllArgsConstructor
    public enum OrdType {
        limit("지정가 주문", PriceType.LIMIT_PRICE),
        price("시장가 주문(매수)", PriceType.BUYING_MARKET_PRICE),
        market("시장가 주문(매도)", PriceType.SELLING_MARKET_PRICE),
        ;
        private String desc;
        private PriceType priceType;

        public static OrdType of(PriceType priceType) {
            return Arrays.stream(OrdType.values())
                .filter(type -> type.getPriceType() == priceType)
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
