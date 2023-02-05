package com.dingdongdeng.coinautotrading.trading.index;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Ma {

    private double sma120;
    private double sma200;
    private double ema60;
}
