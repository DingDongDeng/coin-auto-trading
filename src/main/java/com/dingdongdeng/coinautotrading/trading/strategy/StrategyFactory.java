package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingResult;
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

    public Strategy<?> createStrategy(StrategyServiceParam serviceParam, StrategyCoreParam coreParam) {
        StrategyCode strategyCode = serviceParam.getStrategyCode();
        MarketType marketType = strategyCode.getMarketType();

        if (marketType == MarketType.SPOT) {
            StrategyService<SpotTradingResult> strategyService = new StrategySpotService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (SpotExchangeService) serviceParam.getExchangeService()
            );
            StrategyStore<SpotTradingResult> strategyStore = new StrategyStore<>();
            StrategyCore<SpotTradingResult> strategyCore = (StrategyCore<SpotTradingResult>) strategyCode.getStrategyCore(coreParam);
            StrategyRecorder<SpotTradingResult> strategyRecorder = new StrategySpotRecorder();
            return new Strategy<>(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore, strategyRecorder);
        } else if (marketType == MarketType.FUTURE) {
            StrategyService<FutureTradingResult> strategyService = new StrategyFutureService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (FutureExchangeService) serviceParam.getExchangeService()
            );
            StrategyStore<FutureTradingResult> strategyStore = new StrategyStore<>();
            StrategyCore<FutureTradingResult> strategyCore = (StrategyCore<FutureTradingResult>) strategyCode.getStrategyCore(coreParam);
            StrategyRecorder<FutureTradingResult> strategyRecorder = new StrategyFutureRecorder();
            return new Strategy<>(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore, strategyRecorder);
        } else {
            throw new RuntimeException("not found marketType");
        }
    }

    public StrategyCoreParam createCoreParam(StrategyCode strategyCode, Map<String, Object> strategyCoreParamMap) {
        return objectMapper.convertValue(strategyCoreParamMap, strategyCode.getStrategyCoreParamClazz());
    }
}
