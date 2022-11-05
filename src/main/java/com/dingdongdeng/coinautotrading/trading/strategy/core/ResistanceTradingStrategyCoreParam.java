package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.trading.strategy.annotation.GuideMessage;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResistanceTradingStrategyCoreParam implements StrategyCoreParam {

    @GuideMessage("최초 주문할 금액을 입력해주세요. ex) 40000")
    private double initOrderPrice; // 처음에 주문할 금액

    @GuideMessage("저항선 버퍼(0.002을 설정하면 저항선 기준으로 0.5%의 버퍼를 둠)")
    private double resistancePriceBuffer;

    @GuideMessage("계좌 안전 금액을 입력해주세요.")
    private double accountBalanceLimit;  //계좌 금액 안전 장치

    @GuideMessage("미체결 주문 취소를 위한 대기 시간(second)을 입력해주세요. ex) 30")
    private int tooOldOrderTimeSeconds;  // 초(second)

    @GuideMessage("프로세스 동작 주기(milliseconds)")
    private int processDuration;  // milliseconds

}
