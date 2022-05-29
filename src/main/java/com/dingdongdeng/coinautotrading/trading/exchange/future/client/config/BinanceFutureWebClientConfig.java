package com.dingdongdeng.coinautotrading.trading.exchange.future.client.config;

import com.dingdongdeng.coinautotrading.common.client.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class BinanceFutureWebClientConfig extends WebClientConfig {

    private final BinanceFutureClientResourceProperties properties;

    @Bean
    public WebClient binanceFutureWebClient() {
        return makeWebClient(properties.getBaseUrl(), properties.getReadTimeout(), properties.getConnectionTimeout());
    }

}
