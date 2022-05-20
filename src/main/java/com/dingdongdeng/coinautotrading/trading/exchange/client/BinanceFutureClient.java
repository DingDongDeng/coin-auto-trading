package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureRequest.*;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureResponse.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
public class BinanceFutureClient extends Client {

    private final BinanceFutureTokenGenerator tokenGenerator;

    public BinanceFutureClient(WebClient binanceFutureWebClient, BinanceFutureTokenGenerator tokenGenerator, QueryParamsConverter queryParamsConverter) {
        super(binanceFutureWebClient, queryParamsConverter);
        this.tokenGenerator = tokenGenerator;
    }

    public String getServerTime() {
        return get("/fapi/v1/time", new ParameterizedTypeReference<>() {
        }, new HttpHeaders());
    }

    public List<FuturesAccountBalanceResponse> getFuturesAccountBalance(FuturesAccountBalanceRequest request, String keyPairId) {
        request.setSignature(tokenGenerator.getSignature(request, keyPairId));
        return get("/fapi/v2/balance", request, new ParameterizedTypeReference<>() {
        }, makeHeaders(keyPairId));
    }


    private HttpHeaders makeHeaders(String keyPairId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MBX-APIKEY", tokenGenerator.getAccessKey(keyPairId));
        return headers;
    }

}
