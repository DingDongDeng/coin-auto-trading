package com.dingdongdeng.coinautotrading.common.type;

import java.util.EnumMap;
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

    public static EnumMap<CoinType, String> toMap() {
        EnumMap<CoinType, String> map = new EnumMap<>(CoinType.class);
        for (CoinType value : CoinType.values()) {
            map.put(value, value.getDesc());
        }
        return map;
    }
}
