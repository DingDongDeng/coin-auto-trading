package com.dingdongdeng.autotrading.infra.client.common

import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import java.time.Duration

object RestClientUtils {

    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.Builder.html
    // 타임아웃 설정 관련 : https://github.com/spring-projects/spring-boot/issues/38716

    fun makeRestClient(baseUrl: String, readTimeout: Long, connectionTimeout: Long): RestClient {
        //FIXME
        // MDC 컨텍스트는 잘 유지될까?
        // 요청,응답 로깅은 어떻게 해야할까?

        val requestFactory = ClientHttpRequestFactories.get(
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(readTimeout))
                .withReadTimeout(Duration.ofMillis(connectionTimeout))
        )

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .defaultHeaders { httpHeaders ->
                httpHeaders[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            }
            .requestInterceptors {
                //TODO 로깅 intercepter를 정의... body 여러번 읽을수있나?
            }
            .build()
    }
}