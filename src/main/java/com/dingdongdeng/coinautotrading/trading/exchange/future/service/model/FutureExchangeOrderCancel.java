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

    private Double volume; //총 수량
    private Double cumQuote; //사용된 화폐의 합 ex) 1분동안 도지 10000개가 거래됬는데 들어간 현금
    private Double executedVolume; //채결 수량
    private String orderId; //고유 주문id
    private Double origQty; //사용자가 파라미터에 넣은 quantity(수량)
    private String origType; //사용자가 설정한 매매방법
    private Double price; //채결가
    private Boolean reduceOnly; //인터넷에 reduceOnly검색.(햇지모드일때,closePosition=ture일때 사용불가)
    private OrderType orderType; //매수,매도
    private Position positionSide; //롱,숏
    private OrderState orderState; //포지션의 상태(채결,주문대기,취소...)
    private Double stopPrice; // 주문 유형이 TRALLING_STOP_MARKET인 경우 무시하십시오.
    private Boolean closePosition; // 만약 모든 포지션 종료면 false
    private CoinType coinType; //코인 종류
    private TimeInForceType timeInForceType; //주문의 유효기간 지정 옵션.자세한건 인터넷
    private PriceType priceType; //매매방법(limit,market,TRAILING_STOP_MARKET...등)
    private Double activatePrice; // 활성화 가격, TRAING_STOP_MARKET 주문과 함께 반환만 가능합니다.
    private Double priceRate; // 콜백 요금, TRAILING_STOP_MARKET 주문과 함께만 반환됩니다.
    private LocalDateTime createdAt; //
    private String workingType; //
    private Boolean priceProtect; // 조건부 순서 트리거가 보호되는 경우

}
