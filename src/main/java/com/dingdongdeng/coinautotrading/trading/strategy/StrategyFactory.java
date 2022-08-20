package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingInfo;
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

    public Strategy<?, ?> createStrategy(StrategyServiceParam serviceParam, StrategyCoreParam coreParam) {
        StrategyCode strategyCode = serviceParam.getStrategyCode();
        MarketType marketType = strategyCode.getMarketType();

        if (marketType == MarketType.SPOT) {
            StrategyService<SpotTradingInfo, SpotTradingResult> strategyService = new StrategySpotService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (SpotExchangeService) serviceParam.getExchangeService()
            );
            StrategyStore<SpotTradingResult> strategyStore = new StrategyStore<>();
            StrategyCore<SpotTradingInfo, SpotTradingResult> strategyCore = (StrategyCore<SpotTradingInfo, SpotTradingResult>) strategyCode.getStrategyCore(coreParam);
            return new Strategy<>(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore);
        } else if (marketType == MarketType.FUTURE) {
            StrategyService<FutureTradingInfo, FutureTradingResult> strategyService = new StrategyFutureService(
                serviceParam.getCoinType(),
                serviceParam.getTradingTerm(),
                serviceParam.getKeyPairId(),
                (FutureExchangeService) serviceParam.getExchangeService()
            );
            StrategyStore<FutureTradingResult> strategyStore = new StrategyStore<>();
            StrategyCore<FutureTradingInfo, FutureTradingResult> strategyCore = (StrategyCore<FutureTradingInfo, FutureTradingResult>) strategyCode.getStrategyCore(coreParam);
            return new Strategy<>(serviceParam.getStrategyCode(), strategyCore, strategyService, strategyStore);
        } else {
            throw new RuntimeException("not found marketType");
        }
    }

    public StrategyCoreParam createCoreParam(StrategyCode strategyCode, Map<String, Object> strategyCoreParamMap) {
        return objectMapper.convertValue(strategyCoreParamMap, strategyCode.getStrategyCoreParamClazz());
    }
}
