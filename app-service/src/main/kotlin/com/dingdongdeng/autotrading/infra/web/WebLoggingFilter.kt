package com.dingdongdeng.autotrading.infra.web

import com.dingdongdeng.autotrading.infra.common.log.LoggingUtils
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

class WebLoggingFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        LoggingUtils.trace()
        val cachingRequestWrapper = ContentCachingRequestWrapper(request)
        val cachingResponseWrapper = ContentCachingResponseWrapper(response)

        try {
            val requestBody = objectMapper.readTree(cachingRequestWrapper.contentAsByteArray)
            log.info("[TRANSACTION][REQ] httpMethod=${request.method}, requestUrl=${request.requestURL}, requestBody=$requestBody")
            filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper)
            val responseBody =
                if (hasJsonBody(response)) objectMapper.readTree(cachingResponseWrapper.contentAsByteArray) else objectMapper.createObjectNode()
            log.info("[TRANSACTION][RES] httpMethod=${request.method}, requestUrl=${request.requestURL}, httpStatus=${response.status}, responseBody=${responseBody}")
            cachingResponseWrapper.copyBodyToResponse()
        } catch (e: Exception) {
            log.error(e.message, e)
            throw e
        } finally {
            LoggingUtils.clear()
        }
    }

    private fun hasJsonBody(response: HttpServletResponse): Boolean {
        if (response.contentType == null) {
            return false
        }
        return response.contentType.contains("json")
    }
}