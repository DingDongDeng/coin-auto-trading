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
            log.info(
                "[TRANSACTION][REQ] httpMethod={}, requestUrl={}, requestBody={}",
                request.method, request.requestURL, requestBody
            )
            filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper)
            val responseBody = objectMapper.readTree(cachingResponseWrapper.contentAsByteArray)
            log.info(
                "[TRANSACTION][RES] httpMethod={}, requestUrl={}, httpStatus={}, responseBody={}",
                request.method, request.requestURL, response.status, responseBody
            )
            cachingResponseWrapper.copyBodyToResponse()
        } catch (e: Exception) {
            throw e
        } finally {
            LoggingUtils.clear()
        }
    }
}