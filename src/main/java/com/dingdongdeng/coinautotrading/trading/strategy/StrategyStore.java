package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;

public class StrategyStore {

  private TradingResultPack tradingResultPack = new TradingResultPack();

  public TradingResultPack get() {
    return tradingResultPack;
  }

  public void saveAll(TradingResultPack tradingResultPack) {
    this.tradingResultPack = tradingResultPack;
  }

  public void save(TradingResult tradingResult) {
    tradingResultPack.add(tradingResult);
  }

  public void resetAll() {
    tradingResultPack.reset();
  }

  public void delete(TradingResult tradingResult) {
    tradingResultPack.delete(tradingResult);
  }
}
