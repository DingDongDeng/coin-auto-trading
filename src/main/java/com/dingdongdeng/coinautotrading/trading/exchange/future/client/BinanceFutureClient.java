package com.dingdongdeng.coinautotrading.trading.exchange.future.client;

import com.dingdongdeng.coinautotrading.common.client.ResponseHandler;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureAccountBalanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureCandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureChangeLeverageRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureChangePositionModeRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureMarkPriceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureNewOrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FuturePositionRiskRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.BinanceServerTimeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureAccountBalanceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureCandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureChangeLeverageResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureChangePositionModeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureMarkPriceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureNewOrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderInfoResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FuturePositionRiskResponse;
import java.util.ArrayList;
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
    public List<FutureAccountBalanceResponse> getFuturesAccountBalance(FutureAccountBalanceRequest request, String keyPairId) {
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
        List<FutureCandleResponse> candleResponseList = new ArrayList<>();
        List<List<String>> responseList = responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v1/klines")
                    .queryParams(queryParamsConverter.convertMap(request))
                    .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<String>>>() {
                })
                .block()
        );

        for (List<String> candleInfo: responseList) {
            FutureCandleResponse futureCandleResponse = FutureCandleResponse.builder()
                .openTime(Long.parseLong(candleInfo.get(0)))
                .open(Double.parseDouble(candleInfo.get(1)))
                .high(Double.parseDouble(candleInfo.get(2)))
                .low(Double.parseDouble(candleInfo.get(3)))
                .close(Double.parseDouble(candleInfo.get(4)))
                .volume(Double.parseDouble(candleInfo.get(5)))
                .closeTime(Long.parseLong(candleInfo.get(6)))
                .quoteAssetVolume(Double.parseDouble(candleInfo.get(7)))
                .numberOfTrades(Long.parseLong(candleInfo.get(8)))
                .takerBuyBaseAssetVolume(Double.parseDouble(candleInfo.get(9)))
                .takerBuyQuoteAssetVolume(Double.parseDouble(candleInfo.get(10)))
                .ignore(Long.parseLong(candleInfo.get(11)))
                .build();
            candleResponseList.add(futureCandleResponse);
        }

        return candleResponseList;
    }

    public FutureMarkPriceResponse getMarkPrice(FutureMarkPriceRequest request) {
        return responseHandler.handle(
                () -> binanceFutureWebClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/fapi/v1/premiumIndex")
                                .queryParams(queryParamsConverter.convertMap(request))
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(FutureMarkPriceResponse.class)
                        .block()
        );
    }

    public List<FuturePositionRiskResponse> getPositionRisk(FuturePositionRiskRequest request, String keyPairId) {
        return responseHandler.handle(
            () -> binanceFutureWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fapi/v2/positionRisk")
                    .queryParams(queryParamsConverter.convertMap(makeSignatureWrapper(request, keyPairId)))
                    .build()
                )
                .headers(headers -> headers.addAll(makeHeaders(keyPairId)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<FuturePositionRiskResponse>>() {
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
