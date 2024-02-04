package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

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

    fun <T> future(process: () -> T): CompletableFuture<T> {
        val timeContext = context().now
        return CompletableFuture.supplyAsync {
            update(timeContext)
            process()
        }
    }

    private fun context(): Context = timeContextThreadLocal.get()
}

class Context(
    var now: () -> LocalDateTime = { LocalDateTime.now() }
)

