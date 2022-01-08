package com.dingdongdeng.coinautotrading.exchange.service.model;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExchangeOrderCancel {

    private String orderId; //	주문의 고유 아이디	String
    private OrderType orderType; //	주문 종류	String
    private PriceType priceType; //	주문 방식	String
    private Double price; //	주문 당시 화폐 가격	NumberString
    private String state; //	주문 상태	String
    private String market; //	마켓의 유일키	String
    private String createdAt; //	주문 생성 시간	String
    private Double volume; //	사용자가 입력한 주문 양	NumberString
    private Double remainingVolume; //	체결 후 남은 주문 양	NumberString
    private Double reservedFee; //	수수료로 예약된 비용	NumberString
    private Double remainingFee; //	남은 수수료	NumberString
    private Double paidFee; //	사용된 수수료	NumberString
    private Double locked; //	거래에 사용중인 비용	NumberString
    private Double executedVolume; //	체결된 양	NumberString
    private Long tradeCount; //	해당 주문에 걸린 체결 수	Integer
}
