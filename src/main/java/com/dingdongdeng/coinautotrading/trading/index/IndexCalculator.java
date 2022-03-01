package com.dingdongdeng.coinautotrading.trading.index;

import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles.Candle;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexCalculator {

    private final int RSI_STANDARD_PERIOD = 14;

    /**
     * RSI(지수 가중 이동 평균)
     */
    public double getRsi(ExchangeCandles candles, Double currentPrice) {

        List<Candle> candleList = candles.getCandleList().subList(0, RSI_STANDARD_PERIOD - 1);
        candleList.add(0, Candle.builder().tradePrice(currentPrice).build());
        Collections.reverse(candleList);

        double U = 0d;
        double D = 0d;
        for (int i = 0; i < candleList.size(); i++) {
            if (!hasNext(i, candleList.size())) {
                break;
            }
            double yesterdayPrice = candleList.get(i).getTradePrice();
            double todayPrice = candleList.get(i + 1).getTradePrice();

            if (todayPrice > yesterdayPrice) {
                U += todayPrice - yesterdayPrice;
            } else {
                D += yesterdayPrice - todayPrice;
            }
        }

        double AU = U / RSI_STANDARD_PERIOD;
        double AD = D / RSI_STANDARD_PERIOD;
        for (int i = 0; i < candleList.size(); i++) {
            if (!hasNext(i, candleList.size())) {
                break;
            }
            double yesterdayPrice = candleList.get(i).getTradePrice();
            double todayPrice = candleList.get(i + 1).getTradePrice();

            if (yesterdayPrice < todayPrice) {
                AU = ((RSI_STANDARD_PERIOD - 1) * AU + todayPrice - yesterdayPrice) / RSI_STANDARD_PERIOD;
                AD = ((RSI_STANDARD_PERIOD - 1) * AD + 0) / RSI_STANDARD_PERIOD;
            } else {
                AU = ((RSI_STANDARD_PERIOD - 1) * AU + 0) / RSI_STANDARD_PERIOD;
                AD = ((RSI_STANDARD_PERIOD - 1) * AD + yesterdayPrice - todayPrice) / RSI_STANDARD_PERIOD;
            }
        }

        double RS = AU / AD;
        return RS / (1 + RS);
    }

    private boolean hasNext(int index, int size) {
        return index + 1 < size;
    }
}
