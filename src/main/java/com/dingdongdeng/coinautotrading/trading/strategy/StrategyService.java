package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;

public interface StrategyService<T extends TradingResult> {

    String getKeyPairId();

    TradingInfo getTradingInformation(String identifyCode);

    T order(TradingTask orderTradingTask);

    T orderCancel(TradingTask cancelTradingTask);

    TradingResultPack<T> updateTradingResultPack(TradingResultPack<T> tradingResultPack);

}