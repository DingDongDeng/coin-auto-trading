package com.dingdongdeng.autotrading.domain.autotrade.service

import com.dingdongdeng.autotrading.domain.process.service.ProcessService
import org.springframework.stereotype.Service

@Service
class AutoTradeService(
    private val processService: ProcessService,
) {
    fun register(
        userId: Long,
        autoTradeProcessorId: String,
        isRunnable: () -> Boolean,
        process: () -> Unit,
        duration: Long, //milliseconds
    ): String {
        return processService.register(autoTradeProcessorId, userId, isRunnable, process, duration)
    }

    fun start(autoTradeProcessorId: String) {
        processService.start(autoTradeProcessorId)
    }

    fun stop(autoTradeProcessorId: String) {
        processService.stop(autoTradeProcessorId)
    }

    fun terminate(autoTradeProcessorId: String) {
        processService.terminate(autoTradeProcessorId)
    }
}