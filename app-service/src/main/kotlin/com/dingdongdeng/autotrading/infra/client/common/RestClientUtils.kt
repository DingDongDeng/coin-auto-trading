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
            requestBody: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            val response = execution.execute(request, requestBody)
            log(request, requestBody, response)
            return response
        }

        private fun log(request: HttpRequest, requestBody: ByteArray, response: ClientHttpResponse) {
            if (response.statusCode.isError) {
                log.error(
                    """
                    ====== rest client log ===== 
                    ## request
                    URI: ${request.uri},
                    Method: ${request.method},
                    Headers: ${request.headers},
                    Request body: ${String(requestBody, charset("UTF-8"))},
                    
                    ## response
                    Headers: ${response.headers}
                    Status code: ${response.statusCode},
                    Status text: ${response.statusText},                
                    Response Body: ${StreamUtils.copyToString(response.body, Charset.defaultCharset())}
                """.trimIndent()
                )
            }
        }
    }
}