package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinType {
    ETHEREUM("이더리움"),
    ADA("에이다"),
    XRP("리플"),
    DOGE("도지"),
    ;
    private String desc;
}
