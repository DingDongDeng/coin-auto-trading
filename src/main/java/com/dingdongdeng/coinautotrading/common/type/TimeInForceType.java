package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TimeInForceType {

    GTC("Good Till Cancel"),
    FOK("Fill Or Kill"),
    IOC("Immediate or Cancel"),
    GTX("Good Till Crossing"),
    ;

    private String desc;
}
