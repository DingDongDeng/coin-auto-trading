package com.dingdongdeng.coinautotrading.trading.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradingResultPack {

    private TradingResult buyTradingResult; // 매수 주문
    private TradingResult profitTradingResult; // 익절 주문
    private TradingResult lossTradingResult; // 손절 주문

}
