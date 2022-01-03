package com.dingdongdeng.coinautotrading.exchange.processor.model;

import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.Trade;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ProcessOrderInfoResult {

    private String uuid; // 주문의 고유 아이디
    private String side; // 주문 종류
    private String ordType; // 주문 방식
    private Double price; // 주문 당시 화폐 가격
    private Double avgPrice; // 체결 가격의 평균가
    private String state; // 주문 상태
    private String market; // 마켓의 유일키
    private String createdAt; // 주문 생성 시간
    private Double volume; // 사용자가 입력한 주문 양
    private Double remainingVolume; // 체결 후 남은 주문 양
    private Double reservedFee; // 수수료로 예약된 비용
    private Double remainingFee; // 남은 수수료
    private Double paidFee; // 사용된 수수료
    private Double locked; // 거래에 사용중인 비용
    private Double executedVolume; // 체결된 양
    private Integer tradeCount; // 해당 주문에 걸린 체결 수
    private List<Trade> tradeList; // 체결
}
