package com.dingdongdeng.coinautotrading.common.client;

import com.dingdongdeng.coinautotrading.common.client.exception.ApiResponseException;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
public abstract class Client {

    private final WebClient webClient;
    private final QueryParamsConverter queryParamsConverter;

    protected <T> T get(String path, Class<T> clazz, HttpHeaders headers) {
        return get(path, null, clazz, headers);
    }

    protected <T> T get(String path, Object params, Class<T> clazz, HttpHeaders headers) {
        return get(path, queryParamsConverter.convertMap(params), clazz, headers);
    }

    protected <T> T get(String path, MultiValueMap<String, String> params, Class<T> clazz, HttpHeaders headers) {
        return responseHandle(
            () -> webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(params).build())
                .headers(headers_ -> headers_.addAll(headers))
                .retrieve()
                .bodyToMono(clazz)
                .block()
        );
    }

    protected <T> T get(String path, ParameterizedTypeReference<T> parameterizedTypeReference, HttpHeaders headers) {
        return get(path, null, parameterizedTypeReference, headers);
    }

    protected <T> T get(String path, Object params, ParameterizedTypeReference<T> parameterizedTypeReference, HttpHeaders headers) {
        return get(path, queryParamsConverter.convertMap(params), parameterizedTypeReference, headers);
    }

    protected <T> T get(String path, MultiValueMap<String, String> params, ParameterizedTypeReference<T> parameterizedTypeReference, HttpHeaders headers) {
        return responseHandle(
            () -> webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(params).build())
                .headers(headers_ -> headers_.addAll(headers))
                .retrieve()
                .bodyToMono(parameterizedTypeReference)
                .block()
        );
    }

    protected <T> T post(String path, Object body, Class<T> clazz, HttpHeaders headers) {
        return responseHandle(
            () -> webClient.post()
                .uri(path)
                .headers(headers_ -> headers_.addAll(headers))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(clazz)
                .block()
        );
    }

    protected <T> T put(String path, Object body, Class<T> clazz) {
        return null;
    }

    protected <T> T delete(String path, Object params, Class<T> clazz, HttpHeaders headers) {
        return delete(path, queryParamsConverter.convertMap(params), clazz, headers);
    }

    protected <T> T delete(String path, MultiValueMap params, Class<T> clazz, HttpHeaders headers) {
        return responseHandle(
            () -> webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(params).build())
                .headers(headers_ -> headers_.addAll(headers))
                .retrieve()
                .bodyToMono(clazz)
                .block()
        );
    }

    private <T> T responseHandle(Supplier<T> request) {
        try {
            return request.get();
        } catch (WebClientResponseException e) {
            throw new ApiResponseException(e.getStatusCode(), e.getResponseBodyAsString(StandardCharsets.UTF_8), e);
        } catch (Exception e) {
            throw new ApiResponseException(e);
        }
    }

}
