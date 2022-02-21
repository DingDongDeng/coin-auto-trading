package com.dingdongdeng.coinautotrading.trading.autotrading.model.type;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum Command {
    START,
    STOP,
    TERMINATION,
    ;

    public static Command of(String name) {
        return Arrays.stream(Command.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }
}
