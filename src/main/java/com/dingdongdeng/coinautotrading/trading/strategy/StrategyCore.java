package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;

public interface StrategyCore {

    List<TradingTask> makeTradingTask(TradingInfo tradingInfo);

    void handleOrderResult(TradingResult tradingResult);

    void handleOrderCancelResult(TradingResult tradingResult);

}
