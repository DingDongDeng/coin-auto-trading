package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingCandleLoader;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingProcessor;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleServiceSelector;
import com.dingdongdeng.coinautotrading.trading.index.IndexCalculator;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingService {

    private final StrategyFactory strategyFactory;
    private final ExchangeCandleServiceSelector exchangeCandleServiceSelector;
    private final IndexCalculator indexCalculator;
    private final Map<String, BackTestingProcessor> backTestingProcessorMap; //fixme 한번 래핑해서 다루기

    public BackTestingProcessor doTest(AutoTradingProcessor autoTradingProcessor, LocalDateTime start, LocalDateTime end) {

        String keyPairdId = autoTradingProcessor.getStrategy().getStrategyService().getKeyPairId();
        CoinExchangeType coinExchangeType = autoTradingProcessor.getCoinExchangeType();
        ExchangeCandleService exchangeCandleService = exchangeCandleServiceSelector.getTargetService(coinExchangeType);

        BackTestingCandleLoader backTestingCandleLoader = BackTestingCandleLoader.builder()
            .coinType(autoTradingProcessor.getCoinType())
            .keyPairdId(keyPairdId)
            .exchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
            .build();
        BackTestingContextLoader contextLoader = new BackTestingContextLoader(backTestingCandleLoader, autoTradingProcessor.getTradingTerm());

        BackTestingExchangeService backTestingExchangeService = BackTestingExchangeService.builder()
            .contextLoader(contextLoader)
            .exchangeCandleService(exchangeCandleService)
            .indexCalculator(indexCalculator)
            .build();

        Strategy backTestingStrategy = strategyFactory.create(
            autoTradingProcessor.getStrategy().getStrategyCode(),
            backTestingExchangeService,
            autoTradingProcessor.getCoinType(),
            autoTradingProcessor.getTradingTerm(),
            keyPairdId
        );

        BackTestingProcessor backTestingProcessor = BackTestingProcessor.builder()
            .id(UUID.randomUUID().toString())
            .userId(autoTradingProcessor.getUserId())
            .autoTradingProcessorId(autoTradingProcessor.getId())
            .backTestingStrategy(backTestingStrategy)
            .backTestingContextLoader(contextLoader)
            .duration(200)
            .build();

        backTestingProcessor.start();
        backTestingProcessorMap.put(backTestingProcessor.getId(), backTestingProcessor);
        //fixme processor(backteting, autotrading)안에 recorder라는 클래스 만들어서 필요한 상태들을 저장할수있도록 구조를 만들어보자, 언제 remove할지도 생각

        return backTestingProcessor;
    }

    public List<BackTestingProcessor> getBackTestingProcessorList(String userId) {
        return backTestingProcessorMap.values().stream()
            .filter(processor -> processor.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
}
