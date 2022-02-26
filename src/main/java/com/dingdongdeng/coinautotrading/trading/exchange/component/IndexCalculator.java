package com.dingdongdeng.coinautotrading.trading.exchange.component;

import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles.Candle;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexCalculator {

    private final int RSI_STANDARD_PERIOD = 14;

    public double getRsi(ExchangeCandles candles, Double currentPrice) {
        List<Candle> candleList = candles.getCandleList().subList(0, RSI_STANDARD_PERIOD);
        candleList.add(0, Candle.builder().tradePrice(currentPrice).build());

        double AU = 0d;
        double AD = 0d;
        for (int i = 0; i < candleList.size(); i++) {
            if (!hasNext(i, candleList.size())) {
                break;
            }
            double todayPrice = candleList.get(i).getTradePrice();
            double yesterdayPrice = candleList.get(i + 1).getTradePrice();

            if (todayPrice > yesterdayPrice) {
                AU += todayPrice - yesterdayPrice;
            } else {
                AD += yesterdayPrice - todayPrice;
            }

        }

        double RS = AU / AD;
        return RS / (1 + RS);
    }

    private boolean hasNext(int index, int size) {
        return index + 1 < size;
    }
}
