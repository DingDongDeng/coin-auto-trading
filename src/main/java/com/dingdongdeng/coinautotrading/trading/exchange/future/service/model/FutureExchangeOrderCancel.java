package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.Position;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TimeInForceType;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeOrderCancel {    //https://binance-docs.github.io/apidocs/futures/en/#query-order-user_data

    private String orderId; // 주문의 고유 아이디
    private OrderType orderType; // 주문 종류
    private PriceType priceType; // 주문 방식
    private Double price; // 주문 당시 화폐 가격
    private OrderState orderState; // 주문 상태
    private CoinType coinType;
    private LocalDateTime createdAt; // 주문 생성 시간
    private Double volume; // 사용자가 입력한 주문 양
    private Double executedVolume; // 체결된 양
    private Double cumQuote; //체결에 사용된 금액

}
