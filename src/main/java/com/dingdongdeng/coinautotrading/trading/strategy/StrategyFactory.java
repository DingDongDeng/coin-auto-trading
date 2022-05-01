package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyServiceParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyFactory {

    private final ObjectMapper objectMapper;

    public Strategy create(StrategyServiceParam serviceParam, StrategyCoreParam coreParam) {
        StrategyCode strategyCode = serviceParam.getStrategyCode();
        StrategyService strategyService = new StrategyService(
            serviceParam.getCoinType(),
            serviceParam.getTradingTerm(),
            serviceParam.getKeyPairId(),
            serviceParam.getExchangeService()
        );
        StrategyStore strategyStore = new StrategyStore();
        StrategyCore strategyCore = strategyCode.getStrategyCore(coreParam);
        StrategyRecorder strategyRecorder = new StrategyRecorder();
        return new Strategy(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore, strategyRecorder);
    }

    public StrategyCoreParam createCoreParam(StrategyCode strategyCode, Map<String, Object> strategyCoreParamMap) {
        return objectMapper.convertValue(strategyCoreParamMap, strategyCode.getStrategyCoreParamClazz());
    }
}
