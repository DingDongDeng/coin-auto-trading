package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackTestingTradingTermCalculator {

    private final CandleUnit candleUnit;
    private List<Candle> candleList = new ArrayList<>();

    public void add(Candle currentCandle) {
        if (Objects.isNull(currentCandle)) {
            return;
        }

        if (candleList.size() > candleUnit.getSize()) {
            candleList.remove(0);
        }
        candleList.add(currentCandle);
    }

    public double getCandleAccTradePrice(LocalDateTime from, LocalDateTime to) {
        return this.getByBetweenTime(from, to).stream().mapToDouble(Candle::getCandleAccTradePrice).sum();
    }

    public double getCandleAccTradeVolume(LocalDateTime from, LocalDateTime to) {
        return this.getByBetweenTime(from, to).stream().mapToDouble(Candle::getCandleAccTradeVolume).sum();
    }

    private List<Candle> getByBetweenTime(LocalDateTime from, LocalDateTime to) {
        return this.candleList.stream()
            .filter(candle -> {
                LocalDateTime now = candle.getCandleDateTimeKst();
                // from < current <= to
                return now.isAfter(from) && (now.isBefore(to) || now.isEqual(to));
            })
            .toList();
    }
}
