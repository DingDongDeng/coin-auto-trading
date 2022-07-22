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
public class ScaleTradingRsiV2StrategyCoreParam implements StrategyCoreParam {

    @GuideMessage("매수 주문을 할 rsi 기준을 입력해주세요. ex) 0.30")
    private double buyRsi; // 매수 주문을 할 rsi 기준

    @GuideMessage("익절 이익율을 입력해주세요. ex) 0.1 <== 10% 제한 의미(추매 시 점점 작아짐)")
    private double initProfitRate; // 익절 이익율

    @GuideMessage("최초 주문할 금액을 입력해주세요. ex) 40000")
    private double initOrderPrice; // 처음에 주문할 금액

    @GuideMessage("분할 매수를 할 최초 손실율(0.05을 설정하면 손실율이 5%가 될때마다 매수, 추매 시 점점 커짐)")
    private double initBuyLossRate;

    @GuideMessage("분할 매수를 할때 수량 비율(1를 설정하면 분할 매수할때마다 보유 물량의 1배를 매수)")
    private double buyVolumeRate;

    @GuideMessage("계좌 안전 금액을 입력해주세요.")
    private double accountBalanceLimit;  //계좌 금액 안전 장치

    @GuideMessage("미체결 주문 취소를 위한 대기 시간(second)을 입력해주세요. ex) 30")
    private int tooOldOrderTimeSeconds;  // 초(second)
}
