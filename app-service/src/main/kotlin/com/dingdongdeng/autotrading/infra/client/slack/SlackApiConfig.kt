package com.dingdongdeng.autotrading.infra.client.slack

import com.dingdongdeng.autotrading.utils.WebClientUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SlackApiConfig(
    @Value("\${slack.webHookUrl:}")
    private val webHookUrl: String? = null
) {
    @Bean
    fun slackWebClient(): WebClient {
        return WebClientUtils.makeWebClient(webHookUrl, 5000, 5000)
    }
}
