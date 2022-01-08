package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradingTerm {
    SCALPING("스캘핑"),
    DAY("데이"),
    SWING("스윙"),
    ;

    private String desc;
}
