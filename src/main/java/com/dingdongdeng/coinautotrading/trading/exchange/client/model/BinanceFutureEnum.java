package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class BinanceFutureEnum {

    @Getter
    @AllArgsConstructor
    public enum OrdType {
        limit("지정가 주문", PriceType.LIMIT_PRICE),
        price("시장가 주문(매수)", PriceType.BUYING_MARKET_PRICE),
        market("시장가 주문(매도)", PriceType.SELLING_MARKET_PRICE),
        ;
        private String desc;
        private PriceType priceType;

        public static UpbitEnum.OrdType of(PriceType priceType) {
            return Arrays.stream(UpbitEnum.OrdType.values())
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

        public static UpbitEnum.Side of(OrderType orderType) {
            return Arrays.stream(UpbitEnum.Side.values())
                    .filter(type -> type.getOrderType() == orderType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(orderType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum TimeInForce {
        gtc("매수", OrderType.BUY),
        ask("매도", OrderType.SELL),
        ;

        private String desc;
        private OrderType orderType;

        public static UpbitEnum.Side of(OrderType orderType) {
            return Arrays.stream(UpbitEnum.Side.values())
                    .filter(type -> type.getOrderType() == orderType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(orderType.name()));
        }
    }

}
