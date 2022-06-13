package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
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
        MarketType marketType = strategyCode.getMarketType();
        StrategyService strategyService = makeStrategyService(marketType, serviceParam);
        StrategyStore strategyStore = new StrategyStore();
        StrategyCore strategyCore = strategyCode.getStrategyCore(coreParam);
        StrategyRecorder strategyRecorder = makeStrategyRecorder(marketType);
        return new Strategy(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore, strategyRecorder);
    }

    public StrategyCoreParam createCoreParam(StrategyCode strategyCode, Map<String, Object> strategyCoreParamMap) {
        return objectMapper.convertValue(strategyCoreParamMap, strategyCode.getStrategyCoreParamClazz());
    }

    private StrategyService makeStrategyService(MarketType marketType, StrategyServiceParam serviceParam) {
        if (marketType == MarketType.SPOT) {
            return new StrategySpotService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (SpotExchangeService) serviceParam.getExchangeService()
            );
        }
        if (marketType == MarketType.FUTURE) {
            return new StrategyFutureService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (FutureExchangeService) serviceParam.getExchangeService()
            );

        }
        throw new RuntimeException("not found marketType : " + marketType);
    }

    private StrategyRecorder makeStrategyRecorder(MarketType marketType) {
        if (marketType == MarketType.SPOT) {
            return new StrategySpotRecorder();
        }
        if (marketType == MarketType.FUTURE) {
            return new StrategyFutureRecorder();
        }
    }
}
