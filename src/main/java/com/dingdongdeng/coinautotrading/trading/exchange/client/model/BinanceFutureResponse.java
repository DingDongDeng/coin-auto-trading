package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BinanceFutureResponse {

    @ToString
    @Getter
    public static class BinanceServerTimeResponse{
        @JsonProperty("serverTime")
        private Long serverTime;
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
        private Long updateTime; // 
    }

    @ToString
    @Getter
    public static class FutureChangeLeverageResponse {

        @JsonProperty("leverage")
        private int leverage; // 고유 계정 코드
        @JsonProperty("maxNotionalValue")
        private String maxNotionalValue; // 자산명
        @JsonProperty("symbol")
        private String symbol; // 지갑 잔고

    }

    @ToString
    @Getter
    public static class FutureNewOrderResponse {

        @JsonProperty("clientOrderId")
        private String clientOrderId; //
        @JsonProperty("cumQty")
        private String cumQty; //
        @JsonProperty("executedQty")
        private String executedQty; //
        @JsonProperty("orderId")
        private Long orderId; //
        @JsonProperty("avgPrice")
        private String avgPrice; //
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

}
