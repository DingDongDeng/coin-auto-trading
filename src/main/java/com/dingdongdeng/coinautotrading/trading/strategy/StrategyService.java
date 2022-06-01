package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;

public interface StrategyService {

    String getKeyPairId();

    TradingInfo getTradingInformation(String identifyCode, TradingResultPack tradingResultPack);

    TradingResult order(TradingTask orderTradingTask);

    TradingResult orderCancel(TradingTask cancelTradingTask);

    TradingResultPack updateTradingResultPack(TradingResultPack tradingResultPack);

}