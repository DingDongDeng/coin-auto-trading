package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VirtualCandle extends Candle {

    @Builder(builderMethodName = "virtualCandleBuilder")
    public VirtualCandle(LocalDateTime candleDateTimeUtc, LocalDateTime candleDateTimeKst, Double openingPrice, Double highPrice, Double lowPrice, Double tradePrice,
        Long timestamp,
        Double candleAccTradePrice, Double candleAccTradeVolume) {
        super(candleDateTimeUtc, candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, timestamp, candleAccTradePrice, candleAccTradeVolume);
    }
}
