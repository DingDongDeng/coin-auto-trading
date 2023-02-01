package com.dingdongdeng.coinautotrading.trading.index;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Index {

    private double rsi;
    private List<Double> resistancePriceList;
    private Macd macd;
    private BollingerBands bollingerBands;
    private Obv obv;
}
