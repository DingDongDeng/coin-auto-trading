package com.dingdongdeng.coinautotrading.trading.exchange.future.client.model;

import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.State;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.TimeInForce;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.PositionSide;


@ToString
public class BinanceFutureResponse {

    @ToString
    @Getter
    public static class BinanceServerTimeResponse{  //https://binance-docs.github.io/apidocs/futures/en/#check-server-time
        @JsonProperty("serverTime")
        private Long serverTime;    //서버 시간
    }

    @ToString
    @Getter
    public static class FutureChangePositionModeResponse{   //https://binance-docs.github.io/apidocs/futures/en/#change-position-mode-trade
        @JsonProperty("code")
        private Long code;
        @JsonProperty("msg")
        private String msg;
    }

    @ToString
    @Getter
    public static class FutureAccountBalanceResponse {  //https://binance-docs.github.io/apidocs/futures/en/#futures-account-balance-v2-user_data

        @JsonProperty("accountAlias")
        private String accountAlias; // 고유 계정 코드
        @JsonProperty("asset")
        private String asset; // 자산명
        @JsonProperty("balance")
        private Double balance; // 지갑 잔고
        @JsonProperty("crossWalletBalance")
        private Double crossWalletBalance; // 교차 지갑 잔고
        @JsonProperty("crossUnPnl")
        private Double crossUnPnl; // 교차 직위의 미실현 이익
        @JsonProperty("availableBalance")
        private Double availableBalance; // 사용가능잔액
        @JsonProperty("maxWithdrawAmount")
        private Double maxWithdrawAmount; // 송금 한도액
        @JsonProperty("marginAvailable")
        private boolean marginAvailable; // 자산을 다중 자산 모드에서 여백으로 사용할 수 있는지 여부
        @JsonProperty("updateTime")
        private Long updateTime;
    }

    @ToString
    @Getter
    public static class FutureChangeLeverageResponse {  //https://binance-docs.github.io/apidocs/futures/en/#change-initial-leverage-trade

        @JsonProperty("leverage")
        private int leverage; // 레버리지
        @JsonProperty("maxNotionalValue")
        private Long maxNotionalValue; // 레버리지에 의한 최대 포지션값
        @JsonProperty("symbol")
        private String symbol; // 코인 종류

    }

    @ToString
    @Getter
    public static class FutureOrderInfoResponse {   //https://binance-docs.github.io/apidocs/futures/en/#query-order-user_data

        @JsonProperty("avgPrice")
        private Double avgPrice; //평균가
        @JsonProperty("clientOrderId")
        private String clientOrderId; //고유 id
        @JsonProperty("cumQuote")
        private Double cumQuote; //사용된 화폐의 합 ex) 1분동안 도지 10000개가 거래됬는데 들어간 현금
        @JsonProperty("executedQty")
        private Double executedQty; //채결 수량
        @JsonProperty("orderId")
        private Long orderId; //고유 주문id
        @JsonProperty("origQty")
        private Double origQty; //사용자가 파라미터에 넣은 quantity(수량)
        @JsonProperty("origType")
        private String origType; //사용자가 설정한 매매방법
        @JsonProperty("price")
        private Double price; //채결가
        @JsonProperty("reduceOnly")
        private boolean reduceOnly; //인터넷에 reduceOnly검색.(햇지모드일때,closePosition=ture일때 사용불가)
        @JsonProperty("side")
        private Side side; //매수,매도
        @JsonProperty("positionSide")
        private String positionSide; //롱,숏
        @JsonProperty("status")
        private State status; //포지션의 상태(채결,주문대기,취소...)
        @JsonProperty("stopPrice")
        private Double stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //코인종류
        @JsonProperty("time")
        private Long time;  //주문시간
        @JsonProperty("timeInForce")
        private String timeInForce; //주문의 유효기간 지정 옵션.자세한건 인터넷
        @JsonProperty("type")
        private Type type; //매매방법(limit,market,TRAILING_STOP_MARKET...등)
        @JsonProperty("activatePrice")
        private Double activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private Double priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    public static class FutureNewOrderResponse {    //https://binance-docs.github.io/apidocs/futures/en/#new-order-trade

        @JsonProperty("avgPrice")
        private Double avgPrice; //평균가
        @JsonProperty("clientOrderId")
        private String clientOrderId; //고유 ID
        @JsonProperty("cumQty")
        private Double cumQty; //총 수량
        @JsonProperty("cumQuote")
        private Double cumQuote; //사용된 화폐의 합 ex) 1분동안 도지 10000개가 거래됬는데 들어간 현금
        @JsonProperty("executedQty")
        private Double executedQty; //채결 수량
        @JsonProperty("orderId")
        private Long orderId; //고유 주문id
        @JsonProperty("origQty")
        private Double origQty; //사용자가 파라미터에 넣은 quantity(수량)
        @JsonProperty("price")
        private Double price; //채결가
        @JsonProperty("reduceOnly")
        private boolean reduceOnly; //인터넷에 reduceOnly검색.(햇지모드일때,closePosition=ture일때 사용불가)
        @JsonProperty("side")
        private Side side; //매수,매도
        @JsonProperty("positionSide")
        private String positionSide; //롱,숏
        @JsonProperty("status")
        private State status; //포지션의 상태(채결,주문대기,취소...)
        @JsonProperty("stopPrice")
        private Double stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //코인 종류
        @JsonProperty("timeInForce")
        private String timeInForce; //주문의 유효기간 지정 옵션.자세한건 인터넷
        @JsonProperty("type")
        private Type type; //매매방법(limit,market,TRAILING_STOP_MARKET...등)
        @JsonProperty("origType")
        private String origType; //사용자가 설정한 매매방법
        @JsonProperty("activatePrice")
        private Double activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private Double priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    public static class FutureOrderCancelResponse { //https://binance-docs.github.io/apidocs/futures/en/#cancel-order-trade

        @JsonProperty("clientOrderId")
        private String clientOrderId; //고유 ID
        @JsonProperty("cumQty")
        private Double cumQty; //총 수량
        @JsonProperty("cumQuote")
        private Double cumQuote; //사용된 화폐의 합 ex) 1분동안 도지 10000개가 거래됬는데 들어간 현금
        @JsonProperty("executedQty")
        private Double executedQty; //채결 수량
        @JsonProperty("orderId")
        private Long orderId; //고유 주문id
        @JsonProperty("origQty")
        private Double origQty; //사용자가 파라미터에 넣은 quantity(수량)
        @JsonProperty("origType")
        private String origType; //사용자가 설정한 매매방법
        @JsonProperty("price")
        private Double price; //채결가
        @JsonProperty("reduceOnly")
        private Boolean reduceOnly; //인터넷에 reduceOnly검색.(햇지모드일때,closePosition=ture일때 사용불가)
        @JsonProperty("side")
        private Side side; //매수,매도
        @JsonProperty("positionSide")
        private PositionSide positionSide; //롱,숏
        @JsonProperty("status")
        private State status; //포지션의 상태(채결,주문대기,취소...)
        @JsonProperty("stopPrice")
        private Double stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
        @JsonProperty("closePosition")
        private Boolean closePosition; // 만약 모든 포지션 종료면 false
        @JsonProperty("symbol")
        private String symbol; //코인 종류
        @JsonProperty("timeInForce")
        private TimeInForce timeInForce; //주문의 유효기간 지정 옵션.자세한건 인터넷
        @JsonProperty("type")
        private Type type; //매매방법(limit,market,TRAILING_STOP_MARKET...등)
        @JsonProperty("activatePrice")
        private Double activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
        @JsonProperty("priceRate")
        private Double priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
        @JsonProperty("updateTime")
        private Long updateTime; //
        @JsonProperty("workingType")
        private String workingType; //
        @JsonProperty("priceProtect")
        private Boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

    }

    @ToString
    @Getter
    @Builder
    public static class FutureCandleResponse{   //https://binance-docs.github.io/apidocs/futures/en/#kline-candlestick-data

        private Long openTime;  //캔들시작시간
        private Double open;    //오픈가격
        private Double high;    //봉최고가
        private Double low;     //봉최저가
        private Double close;   //마감가격
        private Double volume;  //거래량
        private Long closeTime; //캔들마감시간
        private Double quoteAssetVolume;    //거래금
        private Long numberOfTrades;    //거래횟수
        private Double takerBuyBaseAssetVolume;
        private Double takerBuyQuoteAssetVolume;
        private Long ignore;

    }

    @ToString
    @Getter
    public static class FutureMarkPriceResponse{    //https://binance-docs.github.io/apidocs/futures/en/#mark-price

        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("markPrice")
        private Double markPrice;
        @JsonProperty("indexPrice")
        private Double indexPrice;
        @JsonProperty("estimatedSettlePrice")
        private Double estimatedSettlePrice;
        @JsonProperty("lastFundingRate")
        private Double lastFundingRate;
        @JsonProperty("nextFundingTime")
        private Long nextFundingTime;
        @JsonProperty("interestRate")
        private Double interestRate;
        @JsonProperty("time")
        private Long time;

    }

}
