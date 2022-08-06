package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketType {
    SPOT("현물"),
    FUTURE("선물"),
    ;

    private String desc;
}
