package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoaderFactory;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.type.BackTestingExchangeFeeType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.index.IndexCalculator;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyFactory;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyServiceParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingService {

    private final StrategyFactory strategyFactory;
    private final BackTestingContextLoaderFactory backTestingContextLoaderFactory;
    private final IndexCalculator indexCalculator;
    private final Map<String, BackTestingProcessor> backTestingProcessorMap; //fixme 한번 래핑해서 다루기, 자동매매 삭제됐을때 얘도 삭제좀;

    public BackTestingProcessor doTest(AutoTradingProcessor autoTradingProcessor, LocalDateTime start, LocalDateTime end) {

        Strategy strategy = autoTradingProcessor.getStrategy();
        String keyPairdId = strategy.getStrategyService().getKeyPairId();
        TradingTerm tradingTerm = autoTradingProcessor.getTradingTerm();

        BackTestingContextLoader contextLoader = backTestingContextLoaderFactory.create(autoTradingProcessor, start, end);

        ExchangeService backTestingExchangeService = makeBackTestingExchangeService(contextLoader, autoTradingProcessor);

        StrategyServiceParam serviceParam = StrategyServiceParam.builder()
            .strategyCode(strategy.getStrategyCode())
            .exchangeService(backTestingExchangeService)
            .coinType(autoTradingProcessor.getCoinType())
            .tradingTerm(tradingTerm)
            .keyPairId(keyPairdId)
            .build();

        Strategy backTestingStrategy = strategyFactory.create(serviceParam, strategy.getStrategyCore().getParam());

        BackTestingProcessor backTestingProcessor = BackTestingProcessor.builder()
            .id("BACKTESTING-" + autoTradingProcessor.getId())
            .userId(autoTradingProcessor.getUserId())
            .autoTradingProcessorId(autoTradingProcessor.getId())
            .start(start)
            .end(end)
            .strategy(backTestingStrategy)
            .backTestingContextLoader(contextLoader)
            .duration(1000)
            .build();

        backTestingProcessor.start();
        backTestingProcessorMap.put(backTestingProcessor.getId(), backTestingProcessor);

        return backTestingProcessor;
    }

    public List<BackTestingProcessor> getBackTestingProcessorList(String userId) {
        return backTestingProcessorMap.values().stream()
            .filter(processor -> processor.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    private ExchangeService makeBackTestingExchangeService(BackTestingContextLoader contextLoader, AutoTradingProcessor autoTradingProcessor) {
        CoinExchangeType coinExchangeType = autoTradingProcessor.getCoinExchangeType();
        BackTestingExchangeFeeType feeType = BackTestingExchangeFeeType.of(autoTradingProcessor.getCoinExchangeType());
        if (coinExchangeType.getMarketType() == MarketType.SPOT) {
            return BackTestingSpotExchangeService.builder()
                .contextLoader(contextLoader)
                .indexCalculator(indexCalculator)
                .exchangeFeeRate(feeType.getFeeRate())
                .build();
        }
        if (coinExchangeType.getMarketType() == MarketType.FUTURE) {
            return BackTestingFutureExchangeService.builder()
                .contextLoader(contextLoader)
                .indexCalculator(indexCalculator)
                .exchangeFeeRate(feeType.getFeeRate())
                .build();
        }
        throw new RuntimeException("백테스팅을 위한 서비스를 생성하지 못함 , MarketType을 매핑하지 못했음");
    }
}
