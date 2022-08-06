package com.dingdongdeng.coinautotrading.trading.exchange.spot.client;

import com.dingdongdeng.coinautotrading.common.client.ResponseHandler;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderBookRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.TickerRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderBookResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrdersChanceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.TickerResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class UpbitClient {

    private final WebClient upbitWebClient;
    private final UpbitTokenGenerator tokenGenerator;
    private final QueryParamsConverter queryParamsConverter;
    private final ResponseHandler responseHandler;

    public List<AccountsResponse> getAccounts(String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri("/v1/accounts")
                .headers(headers -> headers.addAll(makeHeaders(null, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountsResponse>>() {
                })
                .block()
        );
    }

    public OrdersChanceResponse getOrdersChance(OrderChanceRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/orders/chance")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(OrdersChanceResponse.class)
                .block()
        );
    }

    public List<MarketCodeResponse> getMarketList(MarketCodeRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/market/all")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MarketCodeResponse>>() {
                })
                .block()
        );
    }

    public OrderResponse getOrderInfo(OrderInfoRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/order")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block()
        );
    }

    public List<OrderResponse> getOrderInfoList(OrderInfoListRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/orders")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OrderResponse>>() {
                })
                .block()
        );
    }

    public OrderResponse order(OrderRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.post()
                .uri("/v1/orders")
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block()
        );
    }

    public OrderCancelResponse orderCancel(OrderCancelRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/v1/order")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(OrderCancelResponse.class)
                .block()
        );
    }

    public List<CandleResponse> getMinuteCandle(CandleRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/candles/minutes/{unit}")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build(request.getUnit())
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CandleResponse>>() {
                })
                .block()
        );
    }

    public List<OrderBookResponse> getOrderBook(OrderBookRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/orderbook")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OrderBookResponse>>() {
                })
                .block()
        );
    }

    public List<TickerResponse> getTicker(TickerRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/ticker")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(request, keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TickerResponse>>() {
                })
                .block()
        );
    }

    private HttpHeaders makeHeaders(Object request, String keyPairId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenGenerator.makeToken(request, keyPairId));
        return headers;
    }
}
