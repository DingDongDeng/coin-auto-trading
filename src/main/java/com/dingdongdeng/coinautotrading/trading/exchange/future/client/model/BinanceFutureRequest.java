package com.dingdongdeng.coinautotrading.trading.exchange.future.client.model;

import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Interval;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.TimeInForce;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Type;
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
    public static class FutureAccountBalanceRequest {   //https://binance-docs.github.io/apidocs/futures/en/#futures-account-balance-v2-user_data

        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureChangePositionModeRequest {   //https://binance-docs.github.io/apidocs/futures/en/#change-position-mode-trade

        @JsonProperty("dualSidePosition")
        private String dualSidePosition;    //(필수)"true": 헤지 모드 "false": 단방향 모드
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderInfoRequest {    //https://binance-docs.github.io/apidocs/futures/en/#query-order-user_data

        @JsonProperty("symbol")
        private String symbol;  //(필수)코인 종류
        @JsonProperty("orderId")
        private String orderId;   //(필수)주문ID
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureChangeLeverageRequest {   //https://binance-docs.github.io/apidocs/futures/en/#change-initial-leverage-trade

        @JsonProperty("symbol")
        private String symbol;  //(필수)코인 종류
        @JsonProperty("leverage")
        private int leverage;   //(필수)레버리지
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FutureNewOrderRequest {     //https://binance-docs.github.io/apidocs/futures/en/#new-order-trade

        @JsonProperty("symbol")
        private String symbol;  //(필수)코인 종류
        @JsonProperty("side")
        private Side side;  //(필수)매수,매도
        @JsonProperty("type")
        private Type type;  //(필수)주문 종류 - 지정가,시장가,...
        @JsonProperty("price")
        private Double price;   //(지정가 필수값) 진입가
        @JsonProperty("quantity")
        private Double quantity;    //(필수)수량
        @JsonProperty("timeInForce")
        private TimeInForce timeInForce;    //(지정가 필수값) 주문 유형
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    public static class FutureOrderCancelRequest {  //https://binance-docs.github.io/apidocs/futures/en/#cancel-order-trade

        @JsonProperty("symbol")
        private String symbol;  //(필수)코인 종류
        @JsonProperty("orderId")
        private String orderId;   //(필수)주문ID
        @JsonProperty("timestamp")
        private Long timestamp;

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FutureCandleRequest {   //https://binance-docs.github.io/apidocs/futures/en/#kline-candlestick-data

        @JsonProperty("symbol")
        private String symbol;  //(필수)코인 종류
        @JsonProperty("interval")
        private String interval;   //(필수)분봉선택 ex)1분봉,3분봉,5분봉,...
        @JsonProperty("startTime")
        private Long startTime; //(선택)시작시간
        @JsonProperty("endTime")
        private Long endTime;   //(선택)끝시간
        @JsonProperty("limit")
        private Integer limit;

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FutureMarkPriceRequest {    //https://binance-docs.github.io/apidocs/futures/en/#mark-price

        @JsonProperty("symbol")
        private String symbol;  //코인 종류(선택 안할시, 모든 코인의 markPrice반환)

    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class FuturePositionRiskRequest {    //https://binance-docs.github.io/apidocs/futures/en/#position-information-v2-user_data

        @JsonProperty("symbol")
        private String symbol;  //코인 종류(선택 안할시, 모든 코인의 markPrice반환)
        @JsonProperty("recvWindow")
        private Long recvWindow;
        @JsonProperty("timestamp")
        private Long timestamp;

    }

}
