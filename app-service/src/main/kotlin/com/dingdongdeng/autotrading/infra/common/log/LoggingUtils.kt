package com.dingdongdeng.autotrading.infra.common.log

import org.slf4j.MDC
import java.util.Objects
import java.util.UUID

object LoggingUtils {
    private const val TRACE_ID = "traceId"
    fun trace() {
        clear()
        put(TRACE_ID, UUID.randomUUID().toString().replace("-".toRegex(), "").substring(0, 10))
    }

    fun put(key: String, value: String) {
        MDC.put(key, value)
    }

    fun get(key: String): String {
        return MDC.get(key)
    }

    fun getTraceId(): String {
        return MDC.get(TRACE_ID)
    }

    fun getLogData(): Map<String, String> {
        return MDC.getCopyOfContextMap()
    }

    fun setLogData(contextMap: Map<String, String>) {
        if (Objects.nonNull(contextMap)) {
            MDC.setContextMap(contextMap)
        }
    }

    fun clear() {
        MDC.clear()
    }
}
