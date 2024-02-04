package com.dingdongdeng.autotrading.domain.process.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class Processor(
    open val id: String = UUID.randomUUID().toString(),
    open val userId: Long,
    private var status: ProcessStatus = ProcessStatus.INIT,
    private val duration: Long = 60 * 1000, // milliseconds
    private val slackSender: SlackSender?,
) {
    fun start() {
        status = ProcessStatus.RUNNING
        CompletableFuture.runAsync {
            val startTime = System.currentTimeMillis()
            while (status == ProcessStatus.RUNNING && runnable()) {
                try {
                    sleep()
                    process()
                } catch (e: Exception) {
                    log.error("processor 동작 중 실패, id={}, e.meesage={}", id, e.message, e)
                    slackSender?.send("userId : $userId, processorId : $id", e)
                    this.stop()
                    throw e
                }
            }
            val endTime = System.currentTimeMillis()
            val elapsedTime = endTime - startTime

            log.info("프로세서 종료 : {}, {}ms 소요", id, elapsedTime)
        }
    }

    fun stop() {
        status = ProcessStatus.STOPPED
    }

    fun terminate() {
        status = ProcessStatus.TERMINATED
    }

    abstract fun process()

    protected abstract fun runnable(): Boolean

    private fun sleep() {
        if (duration <= 0) {
            return
        }
        Thread.sleep(duration)
    }
}