package com.dingdongdeng.autotrading.domain.process.repository

import com.dingdongdeng.autotrading.domain.process.model.Processor
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import org.springframework.stereotype.Repository

@Repository
class ProcessorRepository {

    private val storedProcessors = mutableMapOf<String, Processor>()

    fun findAll(userId: Long): List<Processor> {
        return storedProcessors.map { it.value }.filter { processor -> processor.userId == userId }
    }

    fun findById(
        processorId: String,
        throwable: Exception = WarnException.of("processor를 찾지 못했습니다. processorId=$processorId")
    ): Processor {
        return storedProcessors[processorId] ?: throw throwable
    }

    fun save(processor: Processor): String {
        storedProcessors[processor.id] = processor
        return processor.id
    }

    fun delete(processor: Processor): String {
        storedProcessors.remove(processor.id)
        return processor.id
    }
}