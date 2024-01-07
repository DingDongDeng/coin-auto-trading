package com.dingdongdeng.autotrading.infra.client.slack

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SlackSender(
    @Value("\${spring.profiles.active:}")
    private val profile: String,
    private val slackRestClient: RestClient,
) {
    fun send(throwable: Throwable) {
        send("occured error ::: ", throwable)
    }

    fun send(message: String, throwable: Throwable) {
        if (profile != "prod") {
            return
        }
        val body: MutableMap<String, String> = HashMap()
        body["username"] = "autotrading"
        body["text"] = """
             $message
             ${throwable.message}
             """.trimIndent()
        try {
            slackRestClient.post()
                .body(body)
                .retrieve()
                .body(Void::class.java)
        } catch (e: Exception) {
            log.error("Failed to send slack message. error : {}, message : {}", e, body)
        }
    }
}
