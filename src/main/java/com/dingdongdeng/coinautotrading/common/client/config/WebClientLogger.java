package com.dingdongdeng.coinautotrading.common.client.config;

import com.dingdongdeng.coinautotrading.common.logging.LoggingUtils;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientLogger implements ExchangeFilterFunction {

    // https://stackoverflow.com/questions/46154994/how-to-log-spring-5-webclient-call
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {

        StringBuilder logReqBuilder = new StringBuilder();
        logReqBuilder
            .append("webclient request ::: ").append("\n")
            .append("url : ").append(request.url()).append("\n")
            .append("method : ").append(request.method()).append("\n")
            .append("headers : ").append(request.headers()).append("\n");
        BodyInserter<?, ? super ClientHttpRequest> originalBodyInserter = request.body();

        Map<String, String> contextMap = LoggingUtils.getLogData();

        ClientRequest loggingClientRequest = ClientRequest.from(request)
            .body((outputMessage, context) -> {
                ClientHttpRequestDecorator loggingOutputMessage = new ClientHttpRequestDecorator(outputMessage) {
                    private final AtomicBoolean alreadyLogged = new AtomicBoolean(false);

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        boolean needToLog = alreadyLogged.compareAndSet(false, true);
                        if (needToLog) {

                            body = DataBufferUtils.join(body)
                                .doOnNext(content -> {
                                    LoggingUtils.setLogData(contextMap);
                                    logReqBuilder.append("body : ").append(content.toString(StandardCharsets.UTF_8)).append("\n");
                                    log.info(logReqBuilder.toString());
                                });
                        }
                        return super.writeWith(body);
                    }

                    @Override
                    public Mono<Void> setComplete() { // This is for requests with no body (e.g. GET).
                        boolean needToLog = alreadyLogged.compareAndSet(false, true);
                        if (needToLog) {
                            log.info(logReqBuilder.toString());
                        }
                        return super.setComplete();
                    }
                };

                return originalBodyInserter.insert(loggingOutputMessage, context);
            })
            .build();

        return next.exchange(loggingClientRequest)
            .map(clientResponse -> {
                    return clientResponse.mutate()
                        .body(f -> f.map(dataBuffer -> {
                            StringBuilder logResBuilder = new StringBuilder();
                            logResBuilder
                                .append("webclient response ::: ").append("\n")
                                .append("status : ").append(clientResponse.statusCode()).append("\n")
                                .append("headers : ").append(clientResponse.headers().asHttpHeaders().toString()).append("\n")
                                .append("body : ").append(dataBuffer.toString(StandardCharsets.UTF_8)).append("\n");
                            log.info(logResBuilder.toString());
                            return dataBuffer;
                        }))
                        .build();
                }
            );

    }
}