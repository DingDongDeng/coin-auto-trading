package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PositionSide;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TimeInForceType;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeOrderCancel {    //https://binance-docs.github.io/apidocs/futures/en/#query-order-user_data

    private Double cumQty; //
    private Double cumQuote; //
    private Double executedQty; //
    private String orderId; //주문의 고유 아이디
    private Double origQty; //
    private String origType; //
    private Double price; //
    private Boolean reduceOnly; //
    private OrderType orderType; //
    private PositionSide positionSide; //
    private OrderState orderState; //
    private Double stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
    private Boolean closePosition; // 만약 모든 포지션 종료면 false
    private CoinType coinType; //
    private TimeInForceType timeInForceType; //
    private PriceType priceType; //
    private Double activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
    private Double priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
    private LocalDateTime updateTime; //
    private String workingType; //fixme 이게 뭘까?
    private Boolean priceProtect; //fixme 이게 뭘까?2 조건부 순서 트리거가 보호되는 경우

}
