package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;

public interface StrategyCore<TI extends TradingInfo, TR extends TradingResult> {

    List<TradingTask> makeTradingTask(TI tradingInfo, TradingResultPack<TR> tradingResultPack);

    void handleOrderResult(TI tradingInfo, TR tradingResult);

    void handleOrderCancelResult(TI tradingInfo, TR tradingResult);

    StrategyCoreParam getParam();

}
