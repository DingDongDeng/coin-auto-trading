package com.dingdongdeng.autotrading.domain.process.service

import com.dingdongdeng.autotrading.domain.process.model.Processor
import org.springframework.stereotype.Service

@Service
class ProcessService {

    private val storedProcessors = mutableMapOf<String, Processor>()

    fun getAll(userId: Long): List<Processor> {
        return storedProcessors.map { it.value }.filter { processor -> processor.userId == userId }
    }

    fun get(processorId: String): Processor {
        return storedProcessors[processorId] ?: throw NoSuchElementException()
    }

    fun register(
        processor: Processor
    ): String {
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