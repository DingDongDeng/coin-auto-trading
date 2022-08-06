package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceType {
    LIMIT("지정가"),
    MARKET("시장가"),
    ;

    private String desc;
}
