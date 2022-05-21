package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureRequest.FuturesAccountBalanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureResponse.FutureAccountBalanceResponse;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class BinanceFutureClient extends Client {

    private final BinanceFutureTokenGenerator tokenGenerator;
    private final BinanceFutureSignatureWrapper signatureWrapper;

    public BinanceFutureClient(WebClient binanceFutureWebClient, BinanceFutureTokenGenerator tokenGenerator, BinanceFutureSignatureWrapper signatureWrapper, QueryParamsConverter queryParamsConverter) {
        super(binanceFutureWebClient, queryParamsConverter);
        this.tokenGenerator = tokenGenerator;
        this.signatureWrapper = signatureWrapper;
    }

    public String getServerTime() {
        return get("/fapi/v1/time", new ParameterizedTypeReference<>() {
        }, new HttpHeaders());
    }

    public List<FutureAccountBalanceResponse> getFuturesAccountBalance(FuturesAccountBalanceRequest request, String keyPairId) {
        return get("/fapi/v2/balance", makeSignatureWrapper(request, keyPairId), new ParameterizedTypeReference<>() {
        }, makeHeaders(keyPairId));
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
