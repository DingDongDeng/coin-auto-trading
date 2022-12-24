package com.dingdongdeng.coinautotrading.trading.index;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import com.dingdongdeng.coinautotrading.trading.index.Index.Macd;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
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
            .current(outMACDHist[outNBElement.value - 1])
            .currentUptrendHighest(this.getCurrentUptrendHighest(outMACDHist))
            .build();
    }

    private double getCurrentUptrendHighest(double[] outMACDHist) {
        double highestMacd = 0;
        for (int i = outMACDHist.length - 1; i >= 0; i--) {
            if (outMACDHist[i] < 0) {
                break;
            }
            if (outMACDHist[i] >= highestMacd) {
                highestMacd = outMACDHist[i];
            }
        }
        return highestMacd;
    }

    public List<Double> getResistancePrice(ExchangeCandles candles) {
        double RESISTANCE_GAP = 0.01; // n% 퍼센트
        int RESISTANCE_MAX_COUNT = 20;

        Map<Double, Double> priceMap = new HashMap<>();
        for (Candle candle : candles.getCandleList()) {
            Double price = candle.getTradePrice();
            Double volume = Objects.isNull(candle.getCandleAccTradeVolume()) ? 0 : candle.getCandleAccTradeVolume();
            if (Objects.isNull(priceMap.get(price))) {
                priceMap.put(price, volume);
            } else {
                priceMap.put(price, priceMap.get(price) + volume);
            }
        }

        // 영향력 있는 지지/저항선을 추출
        List<Entry<Double, Double>> entryList = priceMap.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .limit(RESISTANCE_MAX_COUNT)
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());

        // 저항선 간의 간격이 가깝지 않도록 필터
        // for (int i = 0; i < entryList.size(); i++) {
        //     Entry<Double, Double> currentEntry = entryList.get(i);
        //     for (int j = i + 1; j < entryList.size(); j++) {
        //         Entry<Double, Double> nextEntry = entryList.get(j);
        //         if (currentEntry.getKey() * (1 + RESISTANCE_GAP) > nextEntry.getKey()) {
        //             entryList.remove(nextEntry);
        //             j--;
        //         }
        //     }
        // }

        // 필터 이후에 현재가격 기준으로 저항선이 없다면 추가
        double currentPrice = candles.getLatest(0).getTradePrice();
        double filteredLastResistancePrice = entryList.get(entryList.size() - 1).getKey(); // 필터링 된 후 마지막 저항선 가격
        if (currentPrice > filteredLastResistancePrice) {
            //필터링 되어 저항선이 없다면 간격 조절을 위해 RESISTANCE_GAP만큼 임의 생성
            entryList.add(Map.entry(filteredLastResistancePrice * (1 + RESISTANCE_GAP), 0d));
        }

        return entryList.stream().map(Entry::getKey).collect(Collectors.toList());
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
