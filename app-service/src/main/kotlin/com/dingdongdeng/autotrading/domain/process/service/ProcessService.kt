package com.dingdongdeng.autotrading.domain.process.service

import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProcessService(
    private val slackSender: SlackSender,
) {

    private val storedProcessors = mutableMapOf<String, Processor>()

    fun get(processorId: String): Processor {
        return storedProcessors[processorId] ?: throw NoSuchElementException()
    }

    fun register(
        processorId: String = UUID.randomUUID().toString(),
        userId: Long,
        process: () -> Unit,
        duration: Long
    ): String {
        val processor = Processor(
            id = processorId,
            userId = userId,
            process = process,
            duration = duration,
            slackSender = slackSender
        )

        storedProcessors[processor.id] = processor
        return processor.id
    }

    fun start(processorId: String) {
        storedProcessors[processorId]?.start()
    }

    fun stop(processorId: String) {
        storedProcessors[processorId]?.stop()
    }

    fun terminate(processorId: String) {
        storedProcessors[processorId]?.terminate()
        storedProcessors.remove(processorId)
    }
}