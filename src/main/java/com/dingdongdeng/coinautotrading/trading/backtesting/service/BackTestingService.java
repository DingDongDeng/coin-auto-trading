package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingProcessor;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleServiceSelector;
import com.dingdongdeng.coinautotrading.trading.index.IndexCalculator;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingService {

    private final StrategyFactory strategyFactory;
    private final ExchangeCandleServiceSelector candleServiceSelector;
    private final IndexCalculator indexCalculator;

    public BackTestingProcessor doTest(AutoTradingProcessor autoTradingProcessor, LocalDateTime start, LocalDateTime end) {

        ExchangeCandleService candleService = candleServiceSelector.getTargetService(autoTradingProcessor.getCoinExchangeType());
        BackTestingContextLoader contextLoader = new BackTestingContextLoader(candleService, start, end);

        Strategy backTestingStrategy = strategyFactory.create(
            autoTradingProcessor.getStrategy().getStrategyCode(),
            new BackTestingExchangeService(contextLoader, indexCalculator),
            autoTradingProcessor.getCoinType(),
            autoTradingProcessor.getTradingTerm(),
            null
        );

        BackTestingProcessor backTestingProcessor = BackTestingProcessor.builder()
            .id(UUID.randomUUID().toString())
            .backTestingStrategy(backTestingStrategy)
            .contextLoader(contextLoader)
            .build();

        backTestingProcessor.start();

        return backTestingProcessor;
    }
}
