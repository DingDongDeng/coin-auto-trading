package com.dingdongdeng.autotrading.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class WebClientUtils { //FIXME 나중에 고치자..

    // https://github.com/reactor/reactor-core/issues/1985
    // https://stackoverflow.com/questions/57108631/webclient-how-to-get-request-body
    // https://stackoverflow.com/questions/59307195/spring-webclient-log-uri-and-method-on-response
    // https://stackoverflow.com/questions/46154994/how-to-log-spring-5-webclient-call
    private static final Logger log = LoggerFactory.getLogger(WebClientUtils.class);

    public static WebClient makeWebClient(String baseUrl, int readTimeout, int connectionTimeout) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)))
            //.wiretap("reactor.netty.http.client.HttpClient", LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
            ;

        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders(httpHeaders -> {
                httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            })
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filters(exchangeFilterFunctions -> {
                exchangeFilterFunctions.add(log());
            })
            .build();
    }

    private static ExchangeFilterFunction log() {
        return (ClientRequest clientRequest, ExchangeFunction next) -> {
            var mdcContext = getMdcContext();
            return next.exchange(
                    ClientRequest.from(clientRequest)
                        .body((outputMessage, context) -> clientRequest.body().insert(new ClientHttpRequestDecorator(outputMessage) {
                            @Override
                            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                                MDC.setContextMap(mdcContext);
                                return super.writeWith(Mono.from(body)
                                    .doOnNext(dataBuffer -> log.info("\n"
                                        + "webclient request" + "\n"
                                        + "url : " + clientRequest.url() + "\n"
                                        + "method : " + clientRequest.method() + "\n"
                                        + "headers : " + clientRequest.headers() + "\n"
                                        + "body : " + dataBuffer.toString(StandardCharsets.UTF_8)
                                    ))
                                );
                            }

                            @Override
                            public Mono<Void> setComplete() { // This is for requests with no body (e.g. GET).
                                MDC.setContextMap(mdcContext);
                                log.info("\n"
                                    + "webclient request" + "\n"
                                    + "url : " + clientRequest.url() + "\n"
                                    + "method : " + clientRequest.method() + "\n"
                                    + "headers : " + clientRequest.headers() + "\n"
                                );
                                return super.setComplete();
                            }
                        }, context))
                        .build()

                )
                .map(clientResponse -> clientResponse.mutate()
                    .body(f -> {
                        StringBuilder logResBuilder = new StringBuilder();
                        logResBuilder
                            .append("\n")
                            .append("webclient response").append("\n")
                            .append("url : ").append(clientRequest.url()).append("\n")
                            .append("status : ").append(clientResponse.statusCode()).append("\n")
                            .append("headers : ").append(clientResponse.headers().asHttpHeaders()).append("\n");
                        logResBuilder.append("body : ");
                        return f.map(dataBuffer -> {
                            logResBuilder.append(dataBuffer.toString(StandardCharsets.UTF_8));
                            return dataBuffer;
                        }).doOnComplete(() -> {
                            MDC.setContextMap(mdcContext);
                            log.info(logResBuilder.toString());
                        });
                    })
                    .build()
                );
        };
    }

    private static Map<String, String> getMdcContext() {
        var macdContext = MDC.getCopyOfContextMap();
        if (Objects.isNull(macdContext)) {
            return new HashMap<>();
        }
        return macdContext;
    }
}
