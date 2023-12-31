package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime
import java.util.Objects

object TradingTimeContext {
    private val contextThreadLocal = ThreadLocal.withInitial { Context() }
    fun now(): LocalDateTime {
        val context = contextThreadLocal.get()
        if (Objects.isNull(context)) {
            val newContext = Context()
            contextThreadLocal.set(newContext)
            return newContext.now()
        }
        return context.now()
    }

    fun nowSupplier(supplier: () -> LocalDateTime) {
        val context = contextThreadLocal.get()
        context.now = supplier
    }

    fun clear() {
        contextThreadLocal.remove()
    }
}

class Context(
    var now: () -> LocalDateTime = { LocalDateTime.now() }
)

