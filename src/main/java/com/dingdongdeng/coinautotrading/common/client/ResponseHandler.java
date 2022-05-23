package com.dingdongdeng.coinautotrading.common.client;

import com.dingdongdeng.coinautotrading.common.client.exception.ApiResponseException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResponseHandler {

    public <T> T handle(Supplier<T> request) {
        try {
            return request.get();
        } catch (WebClientResponseException e) {
            throw new ApiResponseException(e.getStatusCode(), e.getResponseBodyAsString(StandardCharsets.UTF_8), e);
        } catch (Exception e) {
            throw new ApiResponseException(e);
        }
    }
}
