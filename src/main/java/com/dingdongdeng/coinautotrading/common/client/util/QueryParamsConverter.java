package com.dingdongdeng.coinautotrading.common.client.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
@Component
public class QueryParamsConverter { //fixme 적절한 이름으로 수정필요

    private final ObjectMapper objectMapper;

    public MultiValueMap<String, String> convert(Object object) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (Objects.isNull(object)) {
                return params;
            }
            Map<String, String> map = objectMapper.convertValue(object, new TypeReference<Map<String, String>>() {
            });
            params.setAll(map);
            return params;
        } catch (Exception e) {
            throw new IllegalStateException("fail generate query params", e);
        }
    }
}
