package com.dingdongdeng.coinautotrading.upbit.client;

import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.upbit.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.upbit.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.upbit.model.UpbitResponse.OrdersChanceResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UpbitClient extends Client {

    private final UpbitClientTokenGenerator tokenGenerator;

    public UpbitClient(WebClient upbitWebClient, UpbitClientTokenGenerator tokenGenerator) {
        super(upbitWebClient);
        this.tokenGenerator = tokenGenerator;
    }

    public List<AccountsResponse> getAccounts() {
        return get("/v1/accounts", List.class, makeHeaders(null));
    }

    public OrdersChanceResponse getOrdersChance(String marketId) {
        String queryParam = "market=" + marketId;
        return get("/v1/orders/chance", queryParam, OrdersChanceResponse.class, makeHeaders(queryParam));
    }

    public List<MarketCodeResponse> getMarketList(boolean isDetail) {
        String queryParam = "isDetails=" + isDetail;
        return get("/v1/market/all", queryParam, List.class, makeHeaders(queryParam));
    }

    private HttpHeaders makeHeaders(String queryParam) {
        HttpHeaders headers = new HttpHeaders();

        if (Objects.nonNull(queryParam)) {
            headers.add("Authorization", tokenGenerator.makeToken(queryParam));
        } else {
            headers.add("Authorization", tokenGenerator.makeToken());
        }
        return headers;
    }
}
