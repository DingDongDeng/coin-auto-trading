package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.ResponseHandler;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureRequest.FuturesAccountBalanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureResponse.FutureAccountBalanceResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@RequiredArgsConstructor
@Component
public class BinanceFutureClient {

    private final WebClient binanceFutureWebClient;
    private final BinanceFutureTokenGenerator tokenGenerator;
    private final BinanceFutureSignatureWrapper signatureWrapper;

    private final QueryParamsConverter queryParamsConverter;
    private final ResponseHandler responseHandler;

    public String getServerTime() {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri("/fapi/v1/time")
                .retrieve()
                .bodyToMono(String.class)
                .block()
        );
    }

    public List<FutureAccountBalanceResponse> getFuturesAccountBalance(FuturesAccountBalanceRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v2/balance")
                    .queryParams(queryParamsConverter.convertMap(makeSignatureWrapper(request, keyPairId)))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<FutureAccountBalanceResponse>>() {
                })
                .block()
        );
    }

    private HttpHeaders makeHeaders(String keyPairId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MBX-APIKEY", tokenGenerator.getAccessKey(keyPairId));
        return headers;
    }

    private Map<String, Object> makeSignatureWrapper(Object request, String keyPairId) {
        String token = tokenGenerator.getSignature(request, keyPairId);
        return signatureWrapper.getSignatureRequest(request, token);
    }

}
