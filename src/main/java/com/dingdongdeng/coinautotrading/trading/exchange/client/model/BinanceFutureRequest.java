package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureEnum.*;
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
    public static class FuturesNewOrderRequest {

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("side")
        private Side side;
        @JsonProperty("type")
        private OrdType type;
        @JsonProperty("timeInForce")
        private OrdType timeInForce;
        @JsonProperty("quantity")
        private OrdType quantity;
        @JsonProperty("timestamp")
        private Long timestamp;
    }

}
