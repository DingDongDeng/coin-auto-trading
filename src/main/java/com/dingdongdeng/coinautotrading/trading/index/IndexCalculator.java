package com.dingdongdeng.coinautotrading.trading.index;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
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
            .resistance(this.getResistancePrice(candles))
            .macd(this.getMACD(candles))
            .bollingerBands(this.getBollingerBands(candles))
            .obv(this.getObv(candles))
            .ma(this.getMv(candles))
            .build();
    }

    public Ma getMv(ExchangeCandles candles) {
        List<Candle> candleList = candles.getCandleList();
        double[] inReal = candleList.stream().mapToDouble(Candle::getTradePrice).toArray();

        // SMA 120
        int SMA120_TIME_PERIOD = 120;
        MInteger sma120OutBegIdx = new MInteger();
        MInteger sma120OutNBElement = new MInteger();
        double[] sma120OutReal = new double[inReal.length];
        core.sma(0, inReal.length - 1, inReal, SMA120_TIME_PERIOD, sma120OutBegIdx, sma120OutNBElement, sma120OutReal);

        // SMA 200
        int SMA200_TIME_PERIOD = 200;
        MInteger sma200OutBegIdx = new MInteger();
        MInteger sma200OutNBElement = new MInteger();
        double[] sma200OutReal = new double[inReal.length];
        core.sma(0, inReal.length - 1, inReal, SMA200_TIME_PERIOD, sma200OutBegIdx, sma200OutNBElement, sma200OutReal);

        // EMA 60
        int EMA60_TIME_PERIOD = 60;
        MInteger ema60OutBegIdx = new MInteger();
        MInteger ema60OutNBElement = new MInteger();
        double[] ema60OutReal = new double[inReal.length];
        core.ema(0, inReal.length - 1, inReal, EMA60_TIME_PERIOD, ema60OutBegIdx, ema60OutNBElement, ema60OutReal);

        return Ma.builder()
            .sma120(sma120OutReal[sma120OutNBElement.value - 1])
            .sma200(sma200OutReal[sma200OutNBElement.value - 1])
            .ema60(ema60OutReal[ema60OutNBElement.value - 1])
            .sma120s(Arrays.copyOfRange(sma120OutReal, 0, sma120OutNBElement.value))
            .build();
    }

    public Obv getObv(ExchangeCandles candles) {
        List<Candle> candleList = candles.getCandleList();

        // obv 계산
        double[] obvInReal = candleList.stream().mapToDouble(Candle::getTradePrice).toArray();
        double[] obvInVolume = candleList.stream().mapToDouble(Candle::getCandleAccTradeVolume).toArray();
        MInteger obvOutBegIdx = new MInteger();
        MInteger obvOutNBElement = new MInteger();
        double[] obvOutReal = new double[obvInReal.length];
        core.obv(0, obvInReal.length - 1, obvInReal, obvInVolume, obvOutBegIdx, obvOutNBElement, obvOutReal);

        // obv 시그널 계산
        int TIME_PERIOD = 10;
        double[] signalInReal = obvOutReal;
        MInteger signalOutBegIdx = new MInteger();
        MInteger signalOutNBElement = new MInteger();
        double[] signalOutReal = new double[signalInReal.length];
        core.sma(0, signalInReal.length - 1, signalInReal, TIME_PERIOD, signalOutBegIdx, signalOutNBElement, signalOutReal);

        return Obv.builder()
            .obv(obvOutReal[obvOutNBElement.value - 1])
            .hist(obvOutReal[obvOutNBElement.value - 1] - signalOutReal[signalOutNBElement.value - 1])
            .build();
    }

    public BollingerBands getBollingerBands(ExchangeCandles candles) {
        List<Candle> candleList = candles.getCandleList();

        // 볼린저 밴드 계산
        MAType MA_TYPE = MAType.Sma;
        int TIME_PERIOD = 20;
        int NB_DEV_UP = 2;
        int NB_DEV_DOWN = 2;
        double[] bbandsInReal = candleList.stream().mapToDouble(Candle::getTradePrice).toArray();
        double[] bbandsOutRealUpperBand = new double[bbandsInReal.length];
        double[] bbandsOutRealMiddleBand = new double[bbandsInReal.length];
        double[] bbandsOutRealLowerBand = new double[bbandsInReal.length];
        MInteger bbandsOutBegIdx = new MInteger();
        MInteger bbandsOutNBElement = new MInteger();

        core.bbands(0, bbandsInReal.length - 1, bbandsInReal, TIME_PERIOD, NB_DEV_UP, NB_DEV_DOWN, MA_TYPE, bbandsOutBegIdx, bbandsOutNBElement, bbandsOutRealUpperBand,
            bbandsOutRealMiddleBand, bbandsOutRealLowerBand);

        // 볼린저 밴드 높이 계산
        double[] bbandsOutRealHeight = new double[bbandsOutNBElement.value];
        for (int i = 0; i < bbandsOutRealHeight.length; i++) {
            bbandsOutRealHeight[i] = bbandsOutRealUpperBand[i] - bbandsOutRealLowerBand[i];
        }

        // 볼린저 밴드 높이 시그널 계산
        int SIGNAL_TIME_PERIOD = 10;
        double[] signalInReal = bbandsOutRealHeight;
        MInteger signalOutBegIdx = new MInteger();
        MInteger signalOutNBElement = new MInteger();
        double[] signalOutReal = new double[signalInReal.length];
        core.sma(0, signalInReal.length - 1, signalInReal, SIGNAL_TIME_PERIOD, signalOutBegIdx, signalOutNBElement, signalOutReal);

        return BollingerBands.builder()
            .upper(bbandsOutRealUpperBand[bbandsOutNBElement.value - 1])
            .middle(bbandsOutRealMiddleBand[bbandsOutNBElement.value - 1])
            .lower(bbandsOutRealLowerBand[bbandsOutNBElement.value - 1])
            .height(bbandsOutRealHeight[bbandsOutNBElement.value - 1])
            .heightHist(bbandsOutRealHeight[bbandsOutNBElement.value - 1] - signalOutReal[signalOutNBElement.value - 1])
            .lowers(Arrays.copyOfRange(bbandsOutRealLowerBand, 0, bbandsOutNBElement.value))
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

    public Resistance getResistancePrice(ExchangeCandles candles) {
        int RESISTANCE_MAX_COUNT = 20;

        // 너무 오래된 저항선은 의미없기때문에 절반의 캔들만 다룸
        int size = candles.getCandleList().size();
        List<Candle> candleList = candles.getCandleList().subList(size / 2, size - 10); //최 캔들은 지지선 계산에서 제외

        // 가격대별 거래량을 통해 지지/저항선을 추출
        Map<Double, Double> priceMap = new HashMap<>();
        for (Candle candle : candleList) {
            Double price = Math.floor(candle.getTradePrice() / 10000) * 10000;
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

        return Resistance.builder()
            .resistancePriceList(priceEntryList.stream().map(Entry::getKey).toList())
            .build();
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
