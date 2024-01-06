package com.dingdongdeng.autotrading.usecase.autotrade.service

import com.dingdongdeng.autotrading.domain.autotrade.service.AutoTradeService
import org.springframework.stereotype.Service

@Service
class CoinAutoTradeManageService(
    private val autoTradeService: AutoTradeService,
) {

    fun register(
        userId: Long,
        autoTradeProcessorId: String,
        process: () -> Unit,
    ): String {
        return autoTradeService.register(autoTradeProcessorId, userId, process, 10000)
    }
}