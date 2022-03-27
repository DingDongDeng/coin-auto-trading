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
    private final IndexCalculator indexCalculator;

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
            .backTestingStrategy(backTestingStrategy)
            .backTestingContextLoader(contextLoader)
            .duration(10)
            .build();

        backTestingProcessor.start();

        return backTestingProcessor;
    }
}
