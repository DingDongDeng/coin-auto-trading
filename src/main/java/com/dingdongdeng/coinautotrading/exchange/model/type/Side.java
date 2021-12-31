package com.dingdongdeng.coinautotrading.exchange.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Side {
    bid("매수"),
    ask("매도"),
    ;

    private String desc;
}
