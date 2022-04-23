package com.dingdongdeng.coinautotrading.trading.strategy.model;

import java.util.List;
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

    private List<TradingResult> buyTradingResultList; // 매수 주문
    private List<TradingResult> profitTradingResultList; // 익절 주문
    private List<TradingResult> lossTradingResultList; // 손절 주문

}
