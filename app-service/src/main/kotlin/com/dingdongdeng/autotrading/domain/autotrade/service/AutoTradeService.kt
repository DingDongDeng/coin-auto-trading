package com.dingdongdeng.autotrading.domain.autotrade.service

import com.dingdongdeng.autotrading.domain.autotrade.model.AutoTradeProcessor
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AutoTradeService(
    private val slackSender: SlackSender,
) {

    private val storedProcessors = mutableMapOf<String, AutoTradeProcessor>()

    fun get(autoTradeProcessorId: String): AutoTradeProcessor {
        return storedProcessors[autoTradeProcessorId] ?: throw NoSuchElementException()
    }

    fun register(
        autoTradeProcessorId: String = UUID.randomUUID().toString(),
        userId: Long,
        process: () -> Unit,
        duration: Long
    ): String {
        val processor = AutoTradeProcessor(
            id = autoTradeProcessorId,
            userId = userId,
            process = process,
            duration = duration,
            slackSender = slackSender
        )

        storedProcessors[processor.id] = processor
        return processor.id
    }

    fun start(autoTradeProcessorId: String) {
        storedProcessors[autoTradeProcessorId]?.start()
    }

    fun stop(autoTradeProcessorId: String) {
        storedProcessors[autoTradeProcessorId]?.stop()
    }

    fun terminate(autoTradeProcessorId: String) {
        storedProcessors[autoTradeProcessorId]?.terminate()
        storedProcessors.remove(autoTradeProcessorId)
    }
}