package com.dingdongdeng.coinautotrading.trading.index;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Index {

    private double rsi;
    private Resistance resistance;
    private Macd macd;
    private BollingerBands bollingerBands;
    private Obv obv;
    private Ma ma;
}
