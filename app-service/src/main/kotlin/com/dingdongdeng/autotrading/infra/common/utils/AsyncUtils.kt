package com.dingdongdeng.autotrading.infra.common.utils

import com.dingdongdeng.autotrading.infra.common.log.LoggingUtils
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object AsyncUtils {
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    fun runAsync(process: () -> Unit) {
        val traceContext = LoggingUtils.getLogData()
        val timeContext = TimeContext.context().now
        CompletableFuture.runAsync {
            LoggingUtils.setLogData(traceContext)
            TimeContext.update(timeContext)
            process()
        }
    }

    fun <T, R> joinAll(list: List<T>, process: (arg: T) -> R): List<R> {
        val traceContext = LoggingUtils.getLogData()
        val timeContext = TimeContext.context().now
        val futures = list.map {
            CompletableFuture.supplyAsync(
                {
                    try {
                        LoggingUtils.setLogData(traceContext)
                        TimeContext.update(timeContext)
                        process(it)
                    } catch (e: Exception) {
                        log.error(e.message, e)
                        throw e
                    }
                },
                executor
            )
        }.toTypedArray()
        CompletableFuture.allOf(*futures).join()
        return futures.map { it.get() }
    }
}