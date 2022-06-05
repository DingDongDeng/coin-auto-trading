package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.selector.SpotExchangeCandleServiceSelector;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackTestingContextLoaderFactory {

    private final SpotExchangeCandleServiceSelector spotExchangeCandleServiceSelector;

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
            .start(getTradingTermStartDateTime(tradingTerm, start))
            .end(end)
            .candleUnit(tradingTerm.getCandleUnit())
            .build();
        return new BackTestingContextLoader(currentCandleLoader, tradingTermCandleLoader);
    }

    private ExchangeCandleService getExchangeCandleService(CoinExchangeType coinExchangeType) {
        return spotExchangeCandleServiceSelector.getTargetService(coinExchangeType);
    }

    private LocalDateTime getTradingTermStartDateTime(TradingTerm tradingTerm, LocalDateTime start) {
        long bufferSize = 200;
        UnitType unitType = tradingTerm.getCandleUnit().getUnitType();
        int unitSize = tradingTerm.getCandleUnit().getSize();

        if (unitType == UnitType.MIN) {
            return start.minusMinutes(unitSize * bufferSize);
        } else if (unitType == UnitType.DAY) {
            return start.minusDays(unitSize * bufferSize);
        } else if (unitType == UnitType.WEEK) {
            return start.minusWeeks(unitSize * bufferSize);
        } else {
            throw new RuntimeException("not found allow unitType");
        }
    }

}
