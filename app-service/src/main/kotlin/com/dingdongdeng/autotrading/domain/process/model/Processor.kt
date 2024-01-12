package com.dingdongdeng.autotrading.domain.process.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.util.*
import java.util.concurrent.CompletableFuture

class Processor(
    val id: String = UUID.randomUUID().toString(),
    val userId: Long,
    var status: ProcessStatus = ProcessStatus.INIT,
    val isRunnable: () -> Boolean,
    val process: () -> Unit,
    val duration: Long = 60 * 1000, // milliseconds
    val slackSender: SlackSender,
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

    private fun sleep() {
        if (duration <= 0) {
            return
        }
        Thread.sleep(duration)
    }
}