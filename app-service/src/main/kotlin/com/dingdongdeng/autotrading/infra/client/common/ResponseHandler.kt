package com.dingdongdeng.autotrading.infra.client.common

import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientResponseException
import java.nio.charset.StandardCharsets

object ResponseHandler {
    fun <T> handle(request: () -> T): T {
        return try {
            request()
        } catch (e: RestClientResponseException) {
            throw ApiResponseException(
                status = HttpStatus.valueOf(e.statusCode.value()),
                body = e.getResponseBodyAsString(StandardCharsets.UTF_8),
                cause = e
            )
        } catch (e: Exception) {
            throw ApiResponseException(cause = e)
        }
    }
}
