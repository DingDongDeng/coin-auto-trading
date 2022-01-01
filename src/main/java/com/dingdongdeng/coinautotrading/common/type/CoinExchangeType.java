package com.dingdongdeng.coinautotrading.common.type;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum CoinExchangeType {
    UPBIT,
    BITHUM,
    ;

    public static CoinExchangeType of(String name) {
        return Arrays.stream(CoinExchangeType.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }
}
