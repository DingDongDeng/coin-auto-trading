package com.dingdongdeng.coinautotrading.common.client;

import com.dingdongdeng.coinautotrading.common.client.exception.ApiResponseException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
public abstract class Client {

    private final WebClient webClient;

    protected <T> T get(String path, Class<T> clazz, HttpHeaders headers) {
        return get(path, "", clazz, headers);
    }

    protected <T> T get(String path, String params, Class<T> clazz, HttpHeaders headers) {
        return responseHandle(
            () -> webClient.get()
                .uri(path + "?" + params)
                .headers(headers_ -> headers_.addAll(headers))
                .retrieve()
                .bodyToMono(clazz)
                .block()
        );
    }

    protected <T> T post(String path, Object body, Class<T> clazz) {
        return null;
    }

    protected <T> T put(String path, Object body, Class<T> clazz) {
        return null;
    }

    protected <T> T delete(String path, String params, Class<T> clazz) {
        return null;
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
