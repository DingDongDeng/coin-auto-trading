package com.dingdongdeng.coinautotrading.trading.exchange.spot.client.config;

import com.dingdongdeng.coinautotrading.common.client.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class UpbitWebClientConfig extends WebClientConfig {

    private final UpbitClientResourceProperties properties;

    @Bean
    public WebClient upbitWebClient() {
        return makeWebClient(properties.getBaseUrl(), properties.getReadTimeout(), properties.getConnectionTimeout());
    }

}
