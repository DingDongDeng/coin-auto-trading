package com.dingdongdeng.coinautotrading.trading.exchange.future.client;

import com.dingdongdeng.coinautotrading.common.client.ResponseHandler;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureCandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureChangeLeverageRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureChangePositionModeRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureNewOrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FuturesAccountBalanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.BinanceServerTimeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureAccountBalanceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureCandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureChangeLeverageResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureChangePositionModeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureNewOrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderInfoResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    /**
     *  서버시간
     */
    public BinanceServerTimeResponse getServerTime() {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri("/fapi/v1/time")
                .retrieve()
                .bodyToMono(BinanceServerTimeResponse.class)
                .block()
        );
    }

    /**
     *  계좌잔고
     */
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

    /**
     *  현재포지션정보
    */
    public FutureOrderInfoResponse getFutureOrderInfo(FutureOrderInfoRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v1/order")
                    .queryParams(queryParamsConverter.convertMap(makeSignatureWrapper(request, keyPairId)))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<FutureOrderInfoResponse>() {
                })
                .block()
        );
    }

    /**
     *  레버리지 바꾸기
     */
    public FutureChangeLeverageResponse changeLeverage(FutureChangeLeverageRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.post()
                .uri("/fapi/v1/leverage")
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(makeQueryParam(request, keyPairId))
                .retrieve()
                .bodyToMono(FutureChangeLeverageResponse.class)
                .block()
        );
    }

    /**
     *  모드 바꾸기(단방향/양방향)
     */
    public FutureChangePositionModeResponse changePositionMode(
        FutureChangePositionModeRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.post()
                .uri("/fapi/v1/positionSide/dual")
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(makeQueryParam(request, keyPairId))
                .retrieve()
                .bodyToMono(FutureChangePositionModeResponse.class)
                .block()
        );
    }

    /**
     *  주문하기
     */
    public FutureNewOrderResponse order(FutureNewOrderRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.post()
                .uri("/fapi/v1/order")
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(makeQueryParam(request, keyPairId))
                .retrieve()
                .bodyToMono(FutureNewOrderResponse.class)
                .block()
        );
    }

    /**
     *  주문취소하기
     */
    public FutureOrderCancelResponse orderCancel(FutureOrderCancelRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v1/order")
                    .queryParams(queryParamsConverter.convertMap(makeSignatureWrapper(request, keyPairId)))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .retrieve()
                .bodyToMono(FutureOrderCancelResponse.class)
                .block()
        );
    }

    /**
     *  캔들조회
     */
    public List<FutureCandleResponse> getMinuteCandle(FutureCandleRequest request) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v1/klines")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<FutureCandleResponse>>() {
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

    private String makeQueryParam(Object request, String keyPairId) {
        return queryParamsConverter.convertStr(makeSignatureWrapper(request, keyPairId)).substring(1);
    }

}
