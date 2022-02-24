package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingProcessStatus;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeServiceSelector;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingService {

    private final AutoTradingManager autoTradingManager;
    private final ExchangeServiceSelector processorSelector;
    private final StrategyFactory strategyFactory;

    public AutoTradingResponse register(AutoTradingRegisterRequest request, String userId) {
        ExchangeService exchangeService = processorSelector.getTargetProcessor(request.getCoinExchangeType());
        Strategy strategy = strategyFactory.create(request.getStrategyCode(), exchangeService, request.getCoinType(), request.getTradingTerm(), request.getKeyPairId());
        AutoTradingProcessor processor = autoTradingManager.register(
            AutoTradingProcessor.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .userId(userId)
                .coinType(request.getCoinType())
                .coinExchangeType(request.getCoinExchangeType())
                .status(AutoTradingProcessStatus.INIT)
                .strategy(strategy)
                .duration(1000)
                .build()
        );
        return makeResponse(processor);
    }

    public AutoTradingResponse start(String processorId, String userId) {
        return makeResponse(autoTradingManager.start(processorId, userId));
    }

    public AutoTradingResponse stop(String processorId, String userId) {
        return makeResponse(autoTradingManager.stop(processorId, userId));
    }

    public AutoTradingResponse terminate(String processorId, String userId) {
        return makeResponse(autoTradingManager.terminate(processorId, userId));
    }

    private AutoTradingResponse makeResponse(AutoTradingProcessor processor) {
        return AutoTradingResponse.builder()
            .title(processor.getTitle())
            .processorId(processor.getId())
            .processDuration(processor.getDuration())
            .processStatus(processor.getStatus())
            .userId(processor.getUserId())
            .strategyCode(processor.getStrategy().getCode())
            .coinType(processor.getCoinType())
            .coinExchangeType(processor.getCoinExchangeType())
            .build();
    }
}
