package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingProcessor;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleServiceSelector;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeServiceSelector;
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
    private final ExchangeCandleServiceSelector exchangeCandleServiceSelector;
    private final ExchangeServiceSelector exchangeServiceSelector;
    private final IndexCalculator indexCalculator;

    public BackTestingProcessor doTest(AutoTradingProcessor autoTradingProcessor, LocalDateTime start, LocalDateTime end) {

        CoinExchangeType coinExchangeType = autoTradingProcessor.getCoinExchangeType();
        ExchangeService exchangeService = exchangeServiceSelector.getTargetService(coinExchangeType);
        ExchangeCandleService exchangeCandleService = exchangeCandleServiceSelector.getTargetService(coinExchangeType);

        BackTestingContextLoader contextLoader = new BackTestingContextLoader(exchangeCandleService, start, end);

        BackTestingExchangeService backTestingExchangeService = BackTestingExchangeService.builder()
            .contextLoader(contextLoader)
            .exchangeService(exchangeService)
            .indexCalculator(indexCalculator)
            .build();

        Strategy backTestingStrategy = strategyFactory.create(
            autoTradingProcessor.getStrategy().getStrategyCode(),
            backTestingExchangeService,
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
