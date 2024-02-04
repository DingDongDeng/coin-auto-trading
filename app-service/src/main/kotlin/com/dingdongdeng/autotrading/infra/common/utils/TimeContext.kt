package com.dingdongdeng.autotrading.infra.common.utils

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object TimeContext {
    private val timeContextThreadLocal = ThreadLocal.withInitial { Context() }
    private val executor = Executors.newFixedThreadPool(100)

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
        return CompletableFuture.supplyAsync(
            {
                try {
                    update(timeContext)
                    process()
                } catch (e: Exception) {
                    log.error(e.message, e)
                    throw e
                }
            },
            executor
        )
    }

    private fun context(): Context = timeContextThreadLocal.get()
}

class Context(
    var now: () -> LocalDateTime = { LocalDateTime.now() }
)

