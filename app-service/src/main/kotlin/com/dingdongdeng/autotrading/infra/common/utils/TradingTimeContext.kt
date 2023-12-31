package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime

object TradingTimeContext {
    private val contextThreadLocal = ThreadLocal.withInitial { Context() }
    fun now(): LocalDateTime {
        val context = contextThreadLocal.get()
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

