package com.dingdongdeng.coinautotrading.upbit.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class UpbitClient {

    private final WebClient upbitWebClient;

    public String getAccounts() {
        return upbitWebClient.get()
            .uri("/v1/accounts")
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
