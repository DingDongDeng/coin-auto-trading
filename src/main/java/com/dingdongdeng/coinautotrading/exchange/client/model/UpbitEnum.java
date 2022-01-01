package com.dingdongdeng.coinautotrading.exchange.client.model;

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
    }

    @Getter
    @AllArgsConstructor
    public enum Side {
        bid("매수"),
        ask("매도"),
        ;

        private String desc;
    }
}
