package com.dingdongdeng.autotrading.infra.client.common

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.web.client.RestClient
import java.nio.charset.Charset
import java.time.Duration


object RestClientUtils {

    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.Builder.html
    // 타임아웃 설정 관련 : https://github.com/spring-projects/spring-boot/issues/38716

    fun makeRestClient(baseUrl: String, readTimeout: Long, connectionTimeout: Long): RestClient {
        //FIXME
        // MDC 컨텍스트는 잘 유지될까?

        val requestFactory = ClientHttpRequestFactories.get(
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(readTimeout))
                .withReadTimeout(Duration.ofMillis(connectionTimeout))
        )

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(BufferingClientHttpRequestFactory(requestFactory))
            .defaultHeaders { httpHeaders ->
                httpHeaders[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            }
            .requestInterceptors {
                it.add(LoggingInterceptor())
            }
            .build()
    }

    // https://medium.com/javarevisited/spring-resttemplate-request-response-logging-f021be66c2c0
    private class LoggingInterceptor : ClientHttpRequestInterceptor {

        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            logRequest(request, body)
            val response = execution.execute(request, body)
            logResponse(request, response)
            return response
        }

        private fun logRequest(request: HttpRequest, body: ByteArray) {
            log.info(
                """
                ===log request start=== 
                URI: {},
                Method: {},
                Headers: {},
                Request body: {},
                ===log request end=== 
                """.trimIndent(),
                request.uri,
                request.method,
                request.headers,
                String(body, charset("UTF-8"))
            )
        }

        private fun logResponse(request: HttpRequest, response: ClientHttpResponse) {
            log.info(
                """
                ===log response start===
                URI: {},
                Status code: {},
                Status text: {},
                Headers: {},
                Response body: {},
                ===log response end===
                """.trimIndent(),
                request.uri,
                response.statusCode,
                response.statusText,
                response.headers,
                StreamUtils.copyToString(response.body, Charset.defaultCharset())
            )
        }
    }
}