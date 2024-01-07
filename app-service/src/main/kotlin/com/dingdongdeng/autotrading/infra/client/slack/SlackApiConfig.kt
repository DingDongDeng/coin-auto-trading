package com.dingdongdeng.autotrading.infra.client.slack

import com.dingdongdeng.autotrading.infra.client.common.RestClientUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class SlackApiConfig(
    @Value("\${slack.webHookUrl:}")
    private val webHookUrl: String? = null
) {
    @Bean
    fun slackRestClient(): RestClient {
        return RestClientUtils.makeRestClient(webHookUrl!!, 5000, 5000)
    }
}
