package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.VirtualCandle;
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
        Candle currentCandle = currentCandleLoader.getNextCandle();
        if (Objects.isNull(currentCandle)) {
            return null;
        }
        return BackTestingContext.builder()
            .coinExchangeType(currentCandleLoader.getCoinExchangeType())
            .coinType(currentCandleLoader.getCoinType())
            .currentPrice(currentCandle.getTradePrice())
            .now(currentCandle.getCandleDateTimeKst())
            .candles(this.getCandles(currentCandle)) // tradingTermCandleLoader를 사용해서 효율적으로 캔들 리스트를 생성
            .build();
    }

    private ExchangeCandles getCandles(Candle currentCandle) {
        List<Candle> candleList = this.getTradingTermCandleList(currentCandle);

        return ExchangeCandles.builder()
            .coinExchangeType(tradingTermCandleLoader.getCoinExchangeType())
            .coinType(tradingTermCandleLoader.getCoinType())
            .candleUnit(tradingTermCandleLoader.getCandleUnit())
            .candleList(candleList)
            .build();
    }

    private List<Candle> getTradingTermCandleList(Candle currentCandle) {
        LocalDateTime currentTime = currentCandle.getCandleDateTimeKst();

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
                candleList.add(this.createVirtualCandle(currentCandle));
            }

            return candleList;
        }

        Candle lastCandle = candleList.get(candleList.size() - 1);
        if (isVirtualCandle(lastCandle)) {
            candleList.remove(lastCandle);
        }

        while (isNeedNextTradingTermCandle(candleList.get(candleList.size() - 1), currentTime)) {
            candleList.add(tradingTermCandleLoader.getNextCandle());
            candleList.remove(0);
        }

        if (!isExistCurrentCandle(currentTime, candleList)) {
            // 현재 시간에 해당하는 캔들이 없어서, 백테스팅에서 시간축으로 사용하는 캔들로 임의 생성
            candleList.add(this.createVirtualCandle(currentCandle));
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

    private boolean isVirtualCandle(Candle candle) {
        if (Objects.isNull(candle)) {
            return false;
        }
        return candle instanceof VirtualCandle;
    }

    private VirtualCandle createVirtualCandle(Candle currentCandle) {
        return VirtualCandle.virtualCandleBuilder()
            .candleDateTimeUtc(currentCandle.getCandleDateTimeUtc())
            .candleDateTimeKst(currentCandle.getCandleDateTimeKst())
            .openingPrice(currentCandle.getOpeningPrice())
            .highPrice(currentCandle.getHighPrice())
            .lowPrice(currentCandle.getLowPrice())
            .tradePrice(currentCandle.getTradePrice())
            .timestamp(currentCandle.getTimestamp())
            .candleAccTradePrice(currentCandle.getCandleAccTradePrice())
            .candleAccTradeVolume(currentCandle.getCandleAccTradeVolume())
            .build();
    }
}
