package com.dingdongdeng.coinautotrading.upbit.client.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@RequiredArgsConstructor
@Configuration
public class WebclientConfig {

    private final ClientResourceProperties properties;

    @Bean
    public WebClient upbitWebClient() {
        return makeWebClient(properties.getBaseUrl(), properties.getReadTimeout(), properties.getConnectionTimeout());
    }

    private WebClient makeWebClient(String baseUrl, int readTimeout, int connectionTimeout) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        //fixme logging filter(++mdc) 필요
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders(httpHeaders -> {
                httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                httpHeaders.set("Authorization", makeAuthenticationToken());
            })
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    private String makeAuthenticationToken() {
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm);

        return "Bearer " + jwtToken;
    }
}
