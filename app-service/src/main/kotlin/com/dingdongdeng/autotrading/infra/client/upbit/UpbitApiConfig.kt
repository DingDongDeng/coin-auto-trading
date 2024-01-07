package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.client.common.RestClientUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@EnableConfigurationProperties(value = [UpbitClientResourceProperties::class])
class UpbitApiConfig {
    @Bean
    fun upbitRestClient(properties: UpbitClientResourceProperties): RestClient {
        return RestClientUtils.makeRestClient(properties.baseUrl, properties.readTimeout, properties.connectionTimeout)
    }
}


@ConfigurationProperties(prefix = "client.upbit")
class UpbitClientResourceProperties(
    val baseUrl: String,
    val readTimeout: Long,
    val connectionTimeout: Long,
)
