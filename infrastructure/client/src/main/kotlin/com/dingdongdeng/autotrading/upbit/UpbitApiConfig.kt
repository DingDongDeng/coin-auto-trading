package com.dingdongdeng.autotrading.upbit

import com.dingdongdeng.autotrading.utils.WebClientUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(value = [UpbitClientResourceProperties::class])
class UpbitApiConfig {
    @Bean
    fun upbitWebClient(properties: UpbitClientResourceProperties): WebClient {
        return WebClientUtils.makeWebClient(properties.baseUrl, properties.readTimeout, properties.connectionTimeout)
    }
}


@ConfigurationProperties(prefix = "client.upbit")
class UpbitClientResourceProperties(
    val baseUrl: String,
    val readTimeout: Int,
    val connectionTimeout: Int,
)
