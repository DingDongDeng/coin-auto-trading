package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles.Candle;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackTestingContextLoader {

    private final BackTestingCandleLoader candleLoader;
    private final TradingTerm tradingTerm;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private BackTestingContext currentContext;

    public boolean hasNext() {
        BackTestingContext context = getNextContext();
        if (Objects.nonNull(context)) {
            this.setCurrentContext(context);
            return true;
        }
        this.setCurrentContext(null);
        return false;
    }

    private BackTestingContext getNextContext() {
        Candle candle = candleLoader.getNextCandle();
        return BackTestingContext.builder()
            .coinExchangeType(candleLoader.getCoinExchangeType())
            .coinType(candleLoader.getCoinType())
            .currentPrice(candle.getTradePrice())
            .now(candle.getCandleDateTimeKst())
            .candles(candleLoader.getCandles(tradingTerm.getCandleUnit(), null, candle.getCandleDateTimeKst()))
            .build();
    }
}
