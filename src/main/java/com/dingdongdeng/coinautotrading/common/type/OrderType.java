package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    BUY("매수"),
    SELL("매도"),
    CANCEL("주문 취소"),
    ;

    private String desc;
}
