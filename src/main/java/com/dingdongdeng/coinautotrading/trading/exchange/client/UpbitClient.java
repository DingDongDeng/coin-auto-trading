package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderBookRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitRequest.TickerRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.OrderBookResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.OrdersChanceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitResponse.TickerResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UpbitClient extends Client {

    private final UpbitTokenGenerator tokenGenerator;

    public UpbitClient(WebClient upbitWebClient, UpbitTokenGenerator tokenGenerator, QueryParamsConverter queryParamsConverter) {
        super(upbitWebClient, queryParamsConverter);
        this.tokenGenerator = tokenGenerator;
    }

    public List<AccountsResponse> getAccounts(String keyPairId) {
        return get("/v1/accounts", new ParameterizedTypeReference<>() {
        }, makeHeaders(null, keyPairId));
    }

    public OrdersChanceResponse getOrdersChance(OrderChanceRequest request, String keyPairId) {
        return get("/v1/orders/chance", request, OrdersChanceResponse.class, makeHeaders(request, keyPairId));
    }

    public List<MarketCodeResponse> getMarketList(MarketCodeRequest request, String keyPairId) {
        return get("/v1/market/all", request, new ParameterizedTypeReference<>() {
        }, makeHeaders(request, keyPairId));
    }

    public OrderResponse getOrderInfo(OrderInfoRequest request, String keyPairId) {
        return get("/v1/order", request, OrderResponse.class, makeHeaders(request, keyPairId));
    }

    public List<OrderResponse> getOrderInfoList(OrderInfoListRequest request, String keyPairId) {
        return get("/v1/orders", request, new ParameterizedTypeReference<>() {
        }, makeHeaders(request, keyPairId));
    }

    public OrderResponse order(OrderRequest request, String keyPairId) {
        return post("/v1/orders", request, OrderResponse.class, makeHeaders(request, keyPairId));
    }

    public OrderCancelResponse orderCancel(OrderCancelRequest request, String keyPairId) {
        return delete("/v1/order", request, OrderCancelResponse.class, makeHeaders(request, keyPairId));
    }

    public List<CandleResponse> getMinuteCandle(CandleRequest request, String keyPairId) {
        return get("/v1/candles/minutes/" + request.getUnit(), request, new ParameterizedTypeReference<>() {
        }, makeHeaders(request, keyPairId));
    }

    public List<OrderBookResponse> getOrderBook(OrderBookRequest request, String keyPairId) {
        return get("/v1/orderbook", request, new ParameterizedTypeReference<>() {
        }, makeHeaders(request, keyPairId));
    }

    public List<TickerResponse> getTicker(TickerRequest request, String keyPairId) {
        return get("/v1/ticker", request, new ParameterizedTypeReference<>() {
        }, makeHeaders(request, keyPairId));
    }

    private HttpHeaders makeHeaders(Object request, String keyPairId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenGenerator.makeToken(request, keyPairId));
        return headers;
    }
}
