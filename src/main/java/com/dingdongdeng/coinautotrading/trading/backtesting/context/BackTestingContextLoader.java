package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.List;
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
        if (Objects.isNull(candle)) {
            return null;
        }
        return BackTestingContext.builder()
            .coinExchangeType(candleLoader.getCoinExchangeType())
            .coinType(candleLoader.getCoinType())
            .currentPrice(candle.getTradePrice())
            .now(candle.getCandleDateTimeKst())
            .candles(getCandles(candle.getTradePrice(), candle.getCandleDateTimeKst()))
            .build();
    }

    private ExchangeCandles getCandles(Double currentPrice, LocalDateTime currentTime) {
        ExchangeCandles candles = candleLoader.getCandles(tradingTerm.getCandleUnit(), null, currentTime); //fixme 백테스팅 속도가 너무 느려져 개선필요
        List<Candle> candleList = candles.getCandleList();
        if (!isExistCurrentCandle(currentTime, candleList)) {
            candleList.add(
                Candle.builder()
                    .candleDateTimeKst(currentTime)
                    .tradePrice(currentPrice)
                    .build()
            );
        }
        return candles;
    }

    private boolean isExistCurrentCandle(LocalDateTime currentTime, List<Candle> candleList) {
        return candleList.get(candleList.size() - 1).getCandleDateTimeKst().equals(currentTime);
    }
}
