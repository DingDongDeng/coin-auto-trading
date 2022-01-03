package com.dingdongdeng.coinautotrading.exchange.processor.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ProcessOrderCancelResult {

    private String uuid; //	주문의 고유 아이디	String
    private String side; //	주문 종류	String
    private String ordType; //	주문 방식	String
    private String price; //	주문 당시 화폐 가격	NumberString
    private String state; //	주문 상태	String
    private String market; //	마켓의 유일키	String
    private String createdAt; //	주문 생성 시간	String
    private String volume; //	사용자가 입력한 주문 양	NumberString
    private String remainingVolume; //	체결 후 남은 주문 양	NumberString
    private String reservedFee; //	수수료로 예약된 비용	NumberString
    private String remainingFee; //	남은 수수료	NumberString
    private String paidFee; //	사용된 수수료	NumberString
    private String locked; //	거래에 사용중인 비용	NumberString
    private String executedVolume; //	체결된 양	NumberString
    private String tradeCount; //	해당 주문에 걸린 체결 수	Integer
}
