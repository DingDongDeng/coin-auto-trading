package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;

public interface StrategyRecorder<T extends TradingResult> {

    void apply(T tradingResult); // 기록

    void revert(T tradingResult); // 기록 취소

    double getTotalFee();

    double getMarginPrice(); // 이익금

    double getMarginRate(); // 이익율

    String getEventMessage();
}
