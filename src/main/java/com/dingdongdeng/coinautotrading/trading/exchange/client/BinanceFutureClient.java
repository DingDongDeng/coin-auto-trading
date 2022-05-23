package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.ResponseHandler;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureRequest.*;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureResponse.*;
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

    public FutureChangeLeverageResponse changeLeverage(FutureChangeLeverageRequest request, String keyPairId) {
        return post("/fapi/v1/leverage?timestamp=" + request.getTimestamp() + "&signature=" + tokenGenerator.getSignature(request,keyPairId), request, FutureChangeLeverageResponse.class, makeHeaders(keyPairId));
    }

    public FutureChangePositionModeResponse changePositionMode(FutureChangePositionModeRequest request, String keyPairId){
        return post("/fapi/v1/positionSide/dual?timestamp=" + request.getTimestamp() + "&signature=" + tokenGenerator.getSignature(request,keyPairId), makeSignatureWrapper(request, keyPairId), FutureChangePositionModeResponse.class, makeHeaders(keyPairId));
    }

    public FutureNewOrderResponse order(FuturesNewOrderRequest request, String keyPairId) {
        return post("/fapi/v1/order", makeSignatureWrapper(request, keyPairId), FutureNewOrderResponse.class, makeHeaders(keyPairId));
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
