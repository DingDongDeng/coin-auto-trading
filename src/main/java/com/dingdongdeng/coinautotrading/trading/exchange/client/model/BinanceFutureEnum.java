package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TimeInForceType;
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
        MARKET("시장가 주문(매도)", PriceType.MARKET),
        ;

        private String desc;
        private PriceType priceType;

        public static BinanceFutureEnum.OrdType of(PriceType priceType) {
            return Arrays.stream(BinanceFutureEnum.OrdType.values())
                    .filter(type -> type.getPriceType() == priceType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(priceType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Side {
        BUY("매수", OrderType.BUY),
        SELL("매도", OrderType.SELL),
        ;

        private String desc;
        private OrderType orderType;

        public static BinanceFutureEnum.Side of(OrderType orderType) {
            return Arrays.stream(BinanceFutureEnum.Side.values())
                    .filter(type -> type.getOrderType() == orderType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(orderType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum TimeInForce {
        gtc("Good Till Cancel", TimeInForceType.GTC),
        fok("Fill Or Kill", TimeInForceType.FOK),
        gtx("Immediate or Cancel", TimeInForceType.GTX),
        ioc("Good Till Crossing", TimeInForceType.IOC),
        ;

        private String desc;
        private TimeInForceType timeInForceType;

        public static BinanceFutureEnum.TimeInForce of(TimeInForceType timeInForceType) {
            return Arrays.stream(BinanceFutureEnum.TimeInForce.values())
                    .filter(type -> type.getTimeInForceType() == timeInForceType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(timeInForceType.name()));
        }
    }

}
