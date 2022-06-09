package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Position {

    SHORT("숏 포지션"),
    LONG("롱 포지션"),
    ;

    private String desc;
}
