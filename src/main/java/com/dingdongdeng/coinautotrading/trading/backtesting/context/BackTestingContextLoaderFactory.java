package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleServiceSelector;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackTestingContextLoaderFactory {

    private final ExchangeCandleServiceSelector exchangeCandleServiceSelector;

    public BackTestingContextLoader create(AutoTradingProcessor autoTradingProcessor, LocalDateTime start, LocalDateTime end) {
        Strategy strategy = autoTradingProcessor.getStrategy();
        String keyPairdId = strategy.getStrategyService().getKeyPairId();
        TradingTerm tradingTerm = autoTradingProcessor.getTradingTerm();
        ExchangeCandleService exchangeCandleService = getExchangeCandleService(autoTradingProcessor.getCoinExchangeType());

        BackTestingCandleLoader currentCandleLoader = BackTestingCandleLoader.builder()
            .coinType(autoTradingProcessor.getCoinType())
            .keyPairdId(keyPairdId)
            .exchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
            .build();
        BackTestingCandleLoader tradingTermCandleLoader = BackTestingCandleLoader.builder()
            .coinType(autoTradingProcessor.getCoinType())
            .keyPairdId(keyPairdId)
            .exchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
            .candleUnit(tradingTerm.getCandleUnit())
            .build();
        return new BackTestingContextLoader(currentCandleLoader, tradingTermCandleLoader);
    }

    private ExchangeCandleService getExchangeCandleService(CoinExchangeType coinExchangeType) {
        return exchangeCandleServiceSelector.getTargetService(coinExchangeType);
    }

}
