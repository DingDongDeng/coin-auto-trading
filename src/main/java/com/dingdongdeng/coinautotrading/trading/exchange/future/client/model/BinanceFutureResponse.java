package com.dingdongdeng.coinautotrading.trading.exchange.future.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BinanceFutureResponse {

    @ToString
    @Getter
    public static class BinanceServerTimeResponse{
        @JsonProperty("serverTime")
        private Long serverTime;    //서버 시간
    }

    @ToString
    @Getter
    public static class FutureChangePositionModeResponse{
        @JsonProperty("code")
        private Long code;
        @JsonProperty("msg")
        private String msg;
    }

    @ToString
    @Getter
    public static class FutureAccountBalanceResponse {

        @JsonProperty("accountAlias")
        private String accountAlias; // 고유 계정 코드
        @JsonProperty("asset")
        private String asset; // 자산명
        @JsonProperty("balance")
        private String balance; // 지갑 잔고
        @JsonProperty("crossWalletBalance")
        private String crossWalletBalance; // 교차 지갑 잔고
        @JsonProperty("crossUnPnl")
        private String crossUnPnl; // 교차 직위의 미실현 이익
        @JsonProperty("availableBalance")
        private String availableBalance; // 사용가능잔액
        @JsonProperty("maxWithdrawAmount")
        private String maxWithdrawAmount; // 송금 한도액
        @JsonProperty("marginAvailable")
        private boolean marginAvailable; // 자산을 다중 자산 모드에서 여백으로 사용할 수 있는지 여부
        @JsonProperty("updateTime")
        private Long updateTime;
    }

    @ToString
    @Getter
    public static class FutureChangeLeverageResponse {

        @JsonProperty("leverage")
        private int leverage; // 레버리지
        @JsonProperty("maxNotionalValue")
        private String maxNotionalValue; // 레버리지에 의한 최대 포지션값
        @JsonProperty("symbol")
        private String symbol; // 코인 종류

    }

    @ToString
    @Getter
    public static class FutureOrderInfoResponse {

        @JsonProperty("avgPrice")
        private String avgPrice; //
        @JsonProperty("clientOrderId")
        private String clientOrderId; //
        @JsonProperty("cumQuote")
        private String cumQuote; //사용된 화폐의 합 ex) 1분동안 도지 10000개가 거래됬는데 들어간 현금
        @JsonProperty("executedQty")
        private String executedQty; //
        @JsonProperty("orderId")
        private Long orderId; //
        @JsonProperty("origQty")
        private String origQty; //
        @JsonProperty("origType")
        private String origType; //
        @JsonProperty("price")
        private String price; //
        @JsonProperty("reduceOnly")
        private boolean reduceOnly; //
        @JsonProperty("side")
        private String side; //
        @JsonProperty("positionSide")
        private String positionSide; //
        @JsonProperty("status")
        private String status; //
        @JsonProperty("stopPrice")
        private String stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //
        @JsonProperty("time")
        private Long time;
        @JsonProperty("timeInForce")
        private String timeInForce; //
        @JsonProperty("type")
        private String type; //
        @JsonProperty("activatePrice")
        private String activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private String priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    public static class FutureNewOrderResponse {

        @JsonProperty("avgPrice")
        private String avgPrice; //
        @JsonProperty("clientOrderId")
        private String clientOrderId; //열려 있는 주문 중 고유한 ID
        @JsonProperty("cumQty")
        private String cumQty; //
        @JsonProperty("cumQuote")
        private String cumQuote; //
        @JsonProperty("executedQty")
        private String executedQty; //
        @JsonProperty("orderId")
        private Long orderId; //
        @JsonProperty("origQty")
        private String origQty; //
        @JsonProperty("price")
        private String price; //
        @JsonProperty("reduceOnly")
        private boolean reduceOnly; //
        @JsonProperty("side")
        private String side; //
        @JsonProperty("positionSide")
        private String positionSide; //
        @JsonProperty("status")
        private String status; //
        @JsonProperty("stopPrice")
        private String stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //
        @JsonProperty("timeInForce")
        private String timeInForce; //
        @JsonProperty("type")
        private String type; //
        @JsonProperty("origType")
        private String origType; //
        @JsonProperty("activatePrice")
        private String activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private String priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    public static class FutureOrderCancelResponse {

        @JsonProperty("clientOrderId")
        private String clientOrderId; //
        @JsonProperty("cumQty")
        private String cumQty; //
        @JsonProperty("cumQuote")
        private String cumQuote; //
        @JsonProperty("executedQty")
        private String executedQty; //
        @JsonProperty("orderId")
        private Long orderId; //
        @JsonProperty("origQty")
        private String origQty; //
        @JsonProperty("origType")
        private String origType; //
        @JsonProperty("price")
        private String price; //
        @JsonProperty("reduceOnly")
        private boolean reduceOnly; //
        @JsonProperty("side")
        private String side; //
        @JsonProperty("positionSide")
        private String positionSide; //
        @JsonProperty("status")
        private String status; //
        @JsonProperty("stopPrice")
        private String stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //
        @JsonProperty("timeInForce")
        private String timeInForce; //
        @JsonProperty("type")
        private String type; //
        @JsonProperty("activatePrice")
        private String activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private String priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FutureCandleResponse{
        @Json   //fixme 응답값받을 방법 구상
        private Long openTime;
        /*@JsonProperty(index = 1)
        private String open;
        @JsonProperty(index = 2)
        private String high;
        @JsonProperty(index = 3)
        private String low;
        @JsonProperty(index = 4)
        private String close;
        @JsonProperty(index = 5)
        private String volume;
        @JsonProperty(index = 6)
        private Long closeTime;
        @JsonProperty(index = 7)
        private String quoteAssetVolume;
        @JsonProperty(index = 8)
        private Long numberOfTrades;
        @JsonProperty(index = 9)
        private String takerBuyBaseAssetVolume;
        @JsonProperty(index = 10)
        private String takerBuyQuoteAssetVolume;
        @JsonProperty(index = 11)
        private String ignore;*/

    }

}
