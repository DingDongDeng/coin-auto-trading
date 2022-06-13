package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import lombok.Getter;

@Getter
public class StrategyFutureRecorder implements StrategyRecorder {

    private double totalBuyPrice;
    private double totalProfitPrice;
    private double totalLossPrice;
    private double totalFee;
    private double marginPrice; // 이익금
    private double marginRate; // 이익율
    private String eventMessage = ""; //fixme 메모리 이슈 가능성이 있음

    @Override
    public void apply(TradingResult tradingResult) {
        //fixme
    }

    @Override
    public void revert(TradingResult tradingResult) {
        //fixme
    }
}
