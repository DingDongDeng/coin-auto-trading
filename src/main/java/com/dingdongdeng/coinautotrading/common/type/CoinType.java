package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinType {
    ETHEREUM("이더리움"),
    ;
    private String desc;
}
