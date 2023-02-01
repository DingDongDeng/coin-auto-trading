package com.dingdongdeng.coinautotrading.trading.index;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Obv {

    private double obv;
    private double prevObv;
    private double diff;
}
