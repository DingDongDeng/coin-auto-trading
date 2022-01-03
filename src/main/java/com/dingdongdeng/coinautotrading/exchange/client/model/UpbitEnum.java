package com.dingdongdeng.coinautotrading.exchange.client.model;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UpbitEnum {

    @Getter
    @AllArgsConstructor
    public enum OrdType {
        limit("지정가 주문"),
        price("시장가 주문(매수)"),
        market("시장가 주문(매도)"),
        ;
        private String desc;

        public static OrdType of(PriceType priceType) {
            if (priceType == PriceType.LIMIT_PRICE) {
                return limit;
            }
            if (priceType == PriceType.BUYING_MARKET_PRICE) {
                return price;
            }

            if (priceType == PriceType.SELLING_MARKET_PRICE) {
                return market;
            }
            throw new NoSuchElementException(priceType.name());
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Side {
        bid("매수"),
        ask("매도"),
        ;

        private String desc;

        public static Side of(OrderType orderType) {
            if (orderType == OrderType.BUYING) {
                return bid;
            }

            if (orderType == OrderType.SELLING) {
                return ask;
            }
            throw new NoSuchElementException(orderType.name());
        }
    }
}
