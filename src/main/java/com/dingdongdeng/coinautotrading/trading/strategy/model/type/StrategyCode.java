package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum StrategyCode {
    RSI,
    ;

    public static StrategyCode of(String name) {
        return Arrays.stream(StrategyCode.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }
}
