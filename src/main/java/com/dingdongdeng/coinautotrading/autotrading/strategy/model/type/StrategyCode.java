package com.dingdongdeng.coinautotrading.autotrading.strategy.model.type;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum StrategyCode {
    PROTOTYPE,
    PROTOTYPE2,
    ;

    public static StrategyCode of(String name) {
        return Arrays.stream(StrategyCode.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }
}
