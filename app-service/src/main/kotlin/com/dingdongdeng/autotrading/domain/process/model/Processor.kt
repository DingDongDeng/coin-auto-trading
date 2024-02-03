package com.dingdongdeng.autotrading.domain.process.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class Processor(
    val id: String = UUID.randomUUID().toString(),
    val userId: Long,
    private var status: ProcessStatus = ProcessStatus.INIT,
    private val isRunnable: () -> Boolean,
    private val duration: Long = 60 * 1000, // milliseconds
    private val slackSender: SlackSender,
) {
    fun start() {
        status = ProcessStatus.RUNNING
        CompletableFuture.runAsync {
            while (status == ProcessStatus.RUNNING && isRunnable()) {
                try {
                    sleep()
                    process()
                } catch (e: Exception) {
                    log.error("processor 동작 중 실패, id={}, e.meesage={}", id, e.message, e)
                    slackSender.send("userId : $userId, processorId : $id", e)
                    this.stop()
                    throw e
                }
            }
        }
    }

    fun stop() {
        status = ProcessStatus.STOPPED
    }

    fun terminate() {
        status = ProcessStatus.TERMINATED
    }

    protected abstract fun process()

    private fun sleep() {
        if (duration <= 0) {
            return
        }
        Thread.sleep(duration)
    }
}