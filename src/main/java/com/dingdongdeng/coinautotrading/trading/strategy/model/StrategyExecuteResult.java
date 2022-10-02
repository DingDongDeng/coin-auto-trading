package com.dingdongdeng.coinautotrading.trading.strategy.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StrategyExecuteResult {

    private TradingInfo tradingInfo;
    private TradingResultPack<? extends TradingResult> tradingResultPack;
    private List<TradingResult> tradingResultList;
}
