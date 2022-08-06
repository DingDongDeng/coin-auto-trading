package com.dingdongdeng.coinautotrading.trading.index;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Index {

    private double rsi;
    private List<Double> resistancePriceList;
    private double macd;
}
