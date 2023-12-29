package com.dingdongdeng.autotrading.infra.client.slack

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SlackSender(
    @Value("\${spring.profiles.active:}")
    private val profile: String,
    private val slackWebClient: WebClient,
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
            slackWebClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void::class.java)
                .retry(2)
                .subscribe(
                    { res -> log.info("slack sended") },
                    { e: Throwable -> log.error(e.message, e) }
                )
        } catch (e: Exception) {
            log.error("Failed to send slack message. error : {}, message : {}", e, body)
        }
    }
}
