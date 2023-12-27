package com.dingdongdeng.autotrading.infra.client.common

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@Component
class QueryParamsConverter(
    private val objectMapper: ObjectMapper
) {
    fun convertMap(body: Any): MultiValueMap<String, String> {
        return try {
            val params: MultiValueMap<String, String> = LinkedMultiValueMap()
            val map = objectMapper.convertValue(body, object : TypeReference<Map<String, Any>>() {})

            map.forEach { (key: String, value: Any) ->
                if (value is List<*>) { //fixme 개선필요
                    value.toList().forEach { params.add(key, it.toString()) }
                    return@forEach
                }
                params.add(key, value.toString())
            }
            params
        } catch (e: Exception) {
            throw IllegalStateException("fail generate query params", e)
        }
    }

    fun convertStr(body: Any): String {
        return UriComponentsBuilder.fromUriString("")
            .queryParams(convertMap(body))
            .build() //.encode()
            .toUriString()
    }
}
