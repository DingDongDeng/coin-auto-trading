package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackTestingContextLoader {

    private final BackTestingCandleLoader currentCandleLoader;
    private final BackTestingCandleLoader tradingTermCandleLoader;
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
        Candle candle = currentCandleLoader.getNextCandle();
        if (Objects.isNull(candle)) {
            return null;
        }
        return BackTestingContext.builder()
            .coinExchangeType(currentCandleLoader.getCoinExchangeType())
            .coinType(currentCandleLoader.getCoinType())
            .currentPrice(candle.getTradePrice())
            .now(candle.getCandleDateTimeKst())
            .candles(getCandles(candle.getTradePrice(), candle.getCandleDateTimeKst()))
            .build();
    }

    private ExchangeCandles getCandles(Double currentPrice, LocalDateTime currentTime) {
        List<Candle> candleList = getTradingTermCandleList(currentPrice, currentTime);

        return ExchangeCandles.builder()
            .coinExchangeType(tradingTermCandleLoader.getCoinExchangeType())
            .coinType(tradingTermCandleLoader.getCoinType())
            .candleUnit(tradingTermCandleLoader.getCandleUnit())
            .candleList(candleList)
            .build();
    }

    private List<Candle> getTradingTermCandleList(Double currentPrice, LocalDateTime currentTime) {
        List<Candle> candleList = Optional.ofNullable(currentContext)
            .map(BackTestingContext::getCandles)
            .map(ExchangeCandles::getCandleList)
            .orElse(new ArrayList<>());

        // 초기화
        if (candleList.isEmpty()) {
            Candle candle = null;
            while (isNeedNextTradingTermCandle(candle, currentTime)) {
                candle = tradingTermCandleLoader.getNextCandle();
                candleList.add(candle);
            }

            if (!isExistCurrentCandle(currentTime, candleList)) {
                candleList.add(
                    Candle.builder()
                        .candleDateTimeKst(currentTime)
                        .tradePrice(currentPrice)
                        .build()
                );
            }

            return candleList;
        }

        if (hasVirtualCandle(candleList)) {
            candleList.remove(candleList.size() - 1);
        }

        while (isNeedNextTradingTermCandle(candleList.get(candleList.size() - 1), currentTime)) {
            candleList.add(tradingTermCandleLoader.getNextCandle());
            candleList.remove(0);
        }

        if (!isExistCurrentCandle(currentTime, candleList)) {
            candleList.add(
                Candle.builder()
                    .candleDateTimeKst(currentTime)
                    .tradePrice(currentPrice)
                    .build()
            );
        }

        return candleList;
    }

    private boolean isNeedNextTradingTermCandle(Candle candle, LocalDateTime currentTime) {
        if (Objects.isNull(candle)) {
            return true;
        }

        LocalDateTime candleDateTime = candle.getCandleDateTimeKst();
        CandleUnit candleUnit = tradingTermCandleLoader.getCandleUnit();
        UnitType unitType = candleUnit.getUnitType();
        if (unitType == UnitType.MIN) {
            return candleDateTime.plusMinutes(candleUnit.getSize()).isBefore(currentTime);
        } else if (unitType == UnitType.DAY) {
            return candleDateTime.plusDays(candleUnit.getSize()).isBefore(currentTime);
        } else if (unitType == UnitType.WEEK) {
            return candleDateTime.plusWeeks(candleUnit.getSize()).isBefore(currentTime);
        } else {
            throw new RuntimeException("not found allow unitType");
        }
    }

    private boolean isExistCurrentCandle(LocalDateTime currentTime, List<Candle> candleList) {
        return candleList.get(candleList.size() - 1).getCandleDateTimeKst().equals(currentTime);
    }

    private boolean hasVirtualCandle(List<Candle> candleList) {
        return Objects.isNull(candleList.get(candleList.size() - 1).getTimestamp());
    }
}
