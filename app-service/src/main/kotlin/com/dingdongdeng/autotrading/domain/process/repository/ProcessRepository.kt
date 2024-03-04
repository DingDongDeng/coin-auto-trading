package com.dingdongdeng.autotrading.domain.process.repository

import com.dingdongdeng.autotrading.domain.process.model.Processor
import org.springframework.stereotype.Repository

@Repository
class ProcessRepository {

    private val storedProcessors = mutableMapOf<String, Processor>()

    fun getAll(userId: Long): List<Processor> {
        return storedProcessors.map { it.value }.filter { processor -> processor.userId == userId }
    }

    fun findById(processorId: String): Processor {
        return storedProcessors[processorId] ?: throw NoSuchElementException()
    }

    fun save(processor: Processor): String {
        storedProcessors[processor.id] = processor
        return processor.id
    }
}