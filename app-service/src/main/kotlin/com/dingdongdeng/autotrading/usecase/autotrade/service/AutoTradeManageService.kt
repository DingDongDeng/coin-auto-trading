package com.dingdongdeng.autotrading.usecase.autotrade.service

import com.dingdongdeng.autotrading.domain.process.service.ProcessService
import org.springframework.stereotype.Service

@Service
class AutoTradeManageService(
    private val processService: ProcessService,
) {

    fun register(
        userId: Long,
        autoTradeProcessorId: String,
        process: () -> Unit,
    ): String {
        return processService.register(autoTradeProcessorId, userId, process, 10000)
    }
}