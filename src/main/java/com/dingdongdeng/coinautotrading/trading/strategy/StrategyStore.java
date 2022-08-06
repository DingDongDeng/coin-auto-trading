package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;

public class StrategyStore<T extends TradingResult> {

    private TradingResultPack<T> tradingResultPack = new TradingResultPack<>();

    public TradingResultPack<T> get() {
        return tradingResultPack;
    }

    public void saveAll(TradingResultPack<T> tradingResultPack) {
        this.tradingResultPack = tradingResultPack;
    }

    public void save(T tradingResult) {
        tradingResultPack.add(tradingResult);
    }

    public void resetAll() {
        tradingResultPack.reset();
    }

    public void delete(T tradingResult) {
        tradingResultPack.delete(tradingResult);
    }
}
