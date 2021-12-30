package com.dingdongdeng.coinautotrading.exchange.upbit.client;

import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.exchange.upbit.model.UpbitResponse.OrdersChanceResponse;
import java.util.List;
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

    public List<AccountsResponse> getAccounts() {
        return get("/v1/accounts", List.class, makeHeaders(null));
    }

    public OrdersChanceResponse getOrdersChance(OrderChanceRequest request) {
        return get("/v1/orders/chance", request, OrdersChanceResponse.class, makeHeaders(request));
    }

    public List<MarketCodeResponse> getMarketList(MarketCodeRequest request) {
        return get("/v1/market/all", request, List.class, makeHeaders(request));
    }

    public OrderResponse order(OrderRequest request) {
        return post("/v1/orders", request, OrderResponse.class, makeHeaders(request));
    }

    public OrderCancelResponse orderCancel(OrderCancelRequest request) {
        return delete("/v1/order", request, OrderCancelResponse.class, makeHeaders(request));
    }

    private HttpHeaders makeHeaders(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenGenerator.makeToken(request));
        return headers;
    }
}
