package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime

object TimeContext {
    private val timeContextThreadLocal = ThreadLocal.withInitial { Context() }

    fun now(): LocalDateTime {
        return context().now()
    }

    fun update(now: () -> LocalDateTime) {
        context().now = now
    }

    fun clear() {
        timeContextThreadLocal.remove()
    }

    fun context(): Context = timeContextThreadLocal.get()
}

class Context(
    var now: () -> LocalDateTime = { LocalDateTime.now() }
)

