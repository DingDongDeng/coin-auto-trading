package com.dingdongdeng.coinautotrading.trading.index;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class BollingerBands {

    private double upper;
    private double middle;
    private double lower;
}
