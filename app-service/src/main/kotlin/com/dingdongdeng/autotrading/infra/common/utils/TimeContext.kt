package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime

object TimeContext {
    private val timeContextThreadLocal = ThreadLocal.withInitial { Context() }
    fun now(): LocalDateTime {
        val context = timeContextThreadLocal.get()
        return context.now()
    }

    fun update(now: () -> LocalDateTime) {
        val context = timeContextThreadLocal.get()
        context.now = now
    }

    fun clear() {
        timeContextThreadLocal.remove()
    }
}

class Context(
    var now: () -> LocalDateTime = { LocalDateTime.now() }
)

