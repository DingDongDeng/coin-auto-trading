package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;

public interface StrategyCore {

    List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo);

    void handleOrderResult(TradingResult tradingResult);

    void handleOrderCancelResult(TradingResult tradingResult);

}
