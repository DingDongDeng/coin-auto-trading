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
        private String dualSidePosition;
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderInfoRequest {

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("orderId")
        private Long orderId;
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureChangeLeverageRequest {

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("leverage")
        private int leverage;
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FutureNewOrderRequest {     //https://binance-docs.github.io/apidocs/futures/en/#new-order-trade

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("side")
        private Side side;
        @JsonProperty("type")
        private Type type;
        @JsonProperty("price")
        private Double price;
        @JsonProperty("quantity")
        private Double quantity;
        @JsonProperty("timeInForce")
        private TimeInForce timeInForce;
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderCancelRequest {

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("orderId")
        private Long orderId;
        @JsonProperty("timestamp")
        private Long timestamp;
    }



}
