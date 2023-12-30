package com.dingdongdeng.autotrading.domain.autotrade.model

import com.dingdongdeng.autotrading.domain.autotrade.type.AutoTradingProcessStatus
import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import java.util.UUID
import java.util.concurrent.CompletableFuture

class AutoTradeProcessor(
    val id: String = UUID.randomUUID().toString(),
    val userId: Long,
    var status: AutoTradingProcessStatus = AutoTradingProcessStatus.INIT,
    val process: () -> Unit,
    val duration: Long = 60 * 1000, // milliseconds
    val slackSender: SlackSender,
) {
    fun start() {
        status = AutoTradingProcessStatus.RUNNING
        CompletableFuture.runAsync {
            while (status == AutoTradingProcessStatus.RUNNING) {
                try {
                    Thread.sleep(duration)
                    process()
                } catch (e: Exception) {
                    log.error("autoTradingProcessor 동작 중 실패, id={}, e.meesage={}", id, e.message, e)
                    slackSender.send("userId : $userId, autoTradingProcessorId : $id", e)
                }
            }
        }
    }

    fun stop() {
        status = AutoTradingProcessStatus.STOPPED
    }

    fun terminate() {
        status = AutoTradingProcessStatus.TERMINATED
    }
}