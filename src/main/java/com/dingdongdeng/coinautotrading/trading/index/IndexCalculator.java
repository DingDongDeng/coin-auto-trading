package com.dingdongdeng.coinautotrading.trading.index;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import com.dingdongdeng.coinautotrading.trading.index.Index.Macd;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexCalculator {

    // document : https://mrjbq7.github.io/ta-lib/doc_index.html
    private final Core core = new Core(); // ta-lib

    public Index getIndex(ExchangeCandles candles) {
        return Index.builder()
            .rsi(this.getRsi(candles))
            .resistancePriceList(this.getResistancePrice(candles))
            .macd(this.getMACD(candles))
            .build();
    }

    public Macd getMACD(ExchangeCandles candles) {
        int FAST_PERIOD = 12;
        int SLOW_PERIOD = 26;
        int SIGNAL_PERIOD = 9;

        List<Candle> candleList = candles.getCandleList();

        double[] inReal = candleList.stream().mapToDouble(Candle::getTradePrice).toArray();
        double[] outMACD = new double[inReal.length];
        double[] outMACDSignal = new double[inReal.length];
        double[] outMACDHist = new double[inReal.length];
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();

        core.macd(0, inReal.length - 1, inReal, FAST_PERIOD, SLOW_PERIOD, SIGNAL_PERIOD, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist);

        return Macd.builder()
            .hist(outMACDHist[outNBElement.value - 1])
            .signal(outMACDSignal[outNBElement.value - 1])
            .macd(outMACD[outNBElement.value - 1])
            .hists(Arrays.copyOfRange(outMACDHist, 0, outNBElement.value))
            .macds(Arrays.copyOfRange(outMACD, 0, outNBElement.value))
            .signals(Arrays.copyOfRange(outMACDSignal, 0, outNBElement.value))
            .build();
    }

    public List<Double> getResistancePrice(ExchangeCandles candles) {
        double RESISTANCE_GAP = 0.02; // n% 퍼센트
        int RESISTANCE_MAX_COUNT = 20;

        // 가격대별 거래량을 통해 지지/저항선을 추출
        Map<Double, Double> priceMap = new HashMap<>();
        for (Candle candle : candles.getCandleList()) {
            Double price = Math.floor(candle.getTradePrice() / 1000) * 1000;
            Double volume = Objects.isNull(candle.getCandleAccTradeVolume()) ? 0 : candle.getCandleAccTradeVolume();
            if (Objects.isNull(priceMap.get(price))) {
                priceMap.put(price, volume);
            } else {
                priceMap.put(price, priceMap.get(price) + volume);
            }
        }

        // 거래량이 많은 지지/저항선을 추출
        List<Entry<Double, Double>> priceEntryList = priceMap.entrySet().stream()
            .sorted(Collections.reverseOrder(Entry.comparingByValue()))
            .limit(RESISTANCE_MAX_COUNT)
            .sorted(Entry.comparingByKey())
            .collect(Collectors.toList());

        // 저항선 간의 간격이 가깝지 않도록 필터
        for (int i = 0; i < priceEntryList.size(); i++) {
            Entry<Double, Double> currentEntry = priceEntryList.get(i);
            for (int j = i + 1; j < priceEntryList.size(); j++) {
                Entry<Double, Double> nextEntry = priceEntryList.get(j);
                if (currentEntry.getKey() * (1 + RESISTANCE_GAP) > nextEntry.getKey()) {
                    priceEntryList.remove(nextEntry);
                    j--;
                }
            }
        }

        // 지지/저항선간의 간격이 큰 것들을 추출
        Map<Double, Double> diffMap = new HashMap<>();
        for (int i = 0; i < priceEntryList.size() - 1; i++) {
            double currentPrice = priceEntryList.get(i).getKey();
            double nextPrice = priceEntryList.get(i + 1).getKey();
            double diffPrice = nextPrice - currentPrice;
            diffMap.put(currentPrice, diffPrice);
        }
        List<Entry<Double, Double>> diffEntryList = diffMap.entrySet().stream()
            .sorted(Collections.reverseOrder(Entry.comparingByValue()))
            .limit(RESISTANCE_MAX_COUNT / 2)
            .sorted(Entry.comparingByKey())
            .toList();

        return diffEntryList.stream().map(Entry::getKey).collect(Collectors.toList());
    }

    // RSI(지수 가중 이동 평균)
    // https://www.investopedia.com/terms/r/rsi.asp
    // https://rebro.kr/139
    public double getRsi(ExchangeCandles candles) {
        int RSI_STANDARD_PERIOD = 14;

        List<Candle> candleList = candles.getCandleList();
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
