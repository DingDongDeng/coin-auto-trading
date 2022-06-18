package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;

public interface StrategyService<TI extends TradingInfo, TR extends TradingResult> {

    String getKeyPairId();

    TI getTradingInformation(String identifyCode);

    TR order(TradingTask orderTradingTask);

    TR orderCancel(TradingTask cancelTradingTask);

    TradingResultPack<TR> updateTradingResultPack(TradingResultPack<TR> tradingResultPack);

}