package com.dingdongdeng.coinautotrading.trading.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StrategyExecuteResult {

    private TradingInfo tradingInfo;
    private TradingResultPack<? extends TradingResult> tradingResultPack;
}
