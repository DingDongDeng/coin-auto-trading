package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class VirtualCandle extends Candle {

    @Setter
    private boolean required;

    @Builder(builderMethodName = "virtualCandleBuilder")
    public VirtualCandle(LocalDateTime candleDateTimeUtc, LocalDateTime candleDateTimeKst, Double openingPrice, Double highPrice, Double lowPrice, Double tradePrice,
        Long timestamp, Double candleAccTradePrice, Double candleAccTradeVolume, boolean required) {
        super(candleDateTimeUtc, candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, timestamp, candleAccTradePrice, candleAccTradeVolume);
        this.required = required;
    }
}
