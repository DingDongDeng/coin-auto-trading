package com.dingdongdeng.coinautotrading.common.type;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinExchangeType {
    UPBIT("업비트"),
    ;

    private String desc;

    public static CoinExchangeType of(String name) {
        return Arrays.stream(CoinExchangeType.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }

    public static EnumMap<CoinExchangeType, String> toMap() {
        EnumMap<CoinExchangeType, String> map = new EnumMap<>(CoinExchangeType.class);
        for (CoinExchangeType value : CoinExchangeType.values()) {
            map.put(value, value.getDesc());
        }
        return map;
    }
}
