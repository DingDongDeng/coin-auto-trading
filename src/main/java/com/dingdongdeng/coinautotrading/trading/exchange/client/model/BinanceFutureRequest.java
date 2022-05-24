package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureEnum.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BinanceFutureRequest {

    @ToString
    @Getter
    @Builder
    public static class FuturesAccountBalanceRequest {

        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureChangePositionModeRequest {

        @JsonProperty("dualSidePosition")
        private String dualSidePosition;    //"true": 헤지 모드 "false": 단방향 모드
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderInfoRequest {

        @JsonProperty("symbol")
        private String symbol;  //코인 종류
        @JsonProperty("orderId")
        private Long orderId;   //주문ID
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureChangeLeverageRequest {

        @JsonProperty("symbol")
        private String symbol;  //코인 종류
        @JsonProperty("leverage")
        private int leverage;   //레버리지
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FutureNewOrderRequest {     //https://binance-docs.github.io/apidocs/futures/en/#new-order-trade

        @JsonProperty("symbol")
        private String symbol;  //코인 종류
        @JsonProperty("side")
        private Side side;  //매수,매도
        @JsonProperty("type")
        private Type type;  //주문 종류
        @JsonProperty("price")
        private Double price;   //(지정가 필수값) 진입가
        @JsonProperty("quantity")
        private Double quantity;    //수량
        @JsonProperty("timeInForce")
        private TimeInForce timeInForce;    //(지정가 필수값) 주문 유형
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderCancelRequest {

        @JsonProperty("symbol")
        private String symbol;  //코인 종류
        @JsonProperty("orderId")
        private Long orderId;   //주문ID
        @JsonProperty("timestamp")
        private Long timestamp;

    }

}
