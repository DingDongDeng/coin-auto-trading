package com.dingdongdeng.coinautotrading.common.logging;

import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtils {

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static Map<String, String> getLogData() {
        return MDC.getCopyOfContextMap();
    }

    public static void setLogData(Map<String, String> contextMap) {
        if (Objects.nonNull(contextMap)) {
            MDC.setContextMap(contextMap);
        }
    }

    public static void clear() {
        MDC.clear();
    }

}
