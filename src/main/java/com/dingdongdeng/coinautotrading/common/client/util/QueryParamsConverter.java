package com.dingdongdeng.coinautotrading.common.client.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class QueryParamsConverter {

    private final ObjectMapper objectMapper;

    public MultiValueMap<String, String> convertMap(Object object) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (Objects.isNull(object)) {
                return params;
            }
            Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<>() {
            });

            map.forEach((key, value) -> {
                if (value instanceof List) { //fixme 개선필요
                    List<String> str = (List<String>) ((List) value).stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList());
                    str.forEach(s -> params.add(key + "[]", s));
                    return;
                }
                params.add(key, String.valueOf(value));
            });
            return params;
        } catch (Exception e) {
            throw new IllegalStateException("fail generate query params", e);
        }
    }

    public String convertStr(Object object) {
        return UriComponentsBuilder.fromUriString("")
            .queryParams(convertMap(object))
            .build()
            //.encode()
            .toUriString();
    }
}
