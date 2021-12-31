package com.dingdongdeng.coinautotrading.exchange.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdType {
    limit("지정가 주문"),
    price("시장가 주문(매수)"),
    market("시장가 주문(매도)"),
    ;
    private String desc;
}
