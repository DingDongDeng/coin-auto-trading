package com.dingdongdeng.coinautotrading.trading.strategy;

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
public class RsiStrategyCoreParam implements StrategyCoreParam {

    private double buyRsi; // 매수 주문을 할 rsi 기준
    private double profitRsi;  // 이익중일때 익절할 rsi 기준
    private double lossRsi;  // 손실중일때 손절할 rsi 기준
    private double profitLimitPriceRate; // 익절 이익율 상한
    private double lossLimitPriceRate;  // 손절 손실율 상한
    private int tooOldOrderTimeSeconds;  // 초(second)
    private double orderPrice; // 한번에 주문할 금액
    private double accountBalanceLimit;  //계좌 금액 안전 장치
}
