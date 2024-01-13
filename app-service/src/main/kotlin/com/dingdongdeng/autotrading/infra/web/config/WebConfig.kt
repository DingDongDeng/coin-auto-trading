package com.dingdongdeng.autotrading.infra.web.config

import com.dingdongdeng.autotrading.infra.web.WebLoggingFilter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class WebConfig {
    @Bean
    fun webLoggingFilter(objectMapper: ObjectMapper): FilterRegistrationBean<*> {
        val filterRegistrationBean = FilterRegistrationBean<WebLoggingFilter>()
        filterRegistrationBean.setFilter(WebLoggingFilter(objectMapper))
        return filterRegistrationBean
    }
}