package com.dingdongdeng.coinautotrading.admin.type;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum Command {
    START,
    STOP,
    ;

    public static Command of(String name) {
        return Arrays.stream(Command.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }
}
