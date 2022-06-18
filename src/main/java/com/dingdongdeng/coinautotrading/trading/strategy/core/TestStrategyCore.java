package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TestStrategyCore implements StrategyCore<FutureTradingResult> {

    private final TestStrategyCoreParam param;

    @Override
    public List<TradingTask> makeTradingTask(TradingInfo<FutureTradingResult> tradingInfo) {

        log.info("msg : {}", param.getMsg());
        log.info("tradingInfo : {}", tradingInfo);
        return List.of();
    }

    @Override
    public void handleOrderResult(FutureTradingResult tradingResult) {

    }

    @Override
    public void handleOrderCancelResult(FutureTradingResult tradingResult) {

    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }
}
