package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyServiceParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyFactory {

    public Strategy create(StrategyServiceParam serviceParam, StrategyCoreParam coreParam) {
        StrategyService strategyService = new StrategyService(
            serviceParam.getCoinType(),
            serviceParam.getTradingTerm(),
            serviceParam.getKeyPairId(),
            serviceParam.getExchangeService()
        );
        StrategyStore strategyStore = new StrategyStore();
        StrategyCore strategyCore = createStrategyCore(serviceParam.getStrategyCode(), coreParam);
        StrategyRecorder strategyRecorder = new StrategyRecorder();
        return new Strategy(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore, strategyRecorder);
    }

    private StrategyCore createStrategyCore(StrategyCode strategyCode, StrategyCoreParam coreParam) {
        if (strategyCode == StrategyCode.RSI) {
            return new RsiStrategyCore(coreParam);
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
