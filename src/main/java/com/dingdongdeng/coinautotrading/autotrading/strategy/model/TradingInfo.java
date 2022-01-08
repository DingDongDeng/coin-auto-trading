package com.dingdongdeng.coinautotrading.autotrading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class TradingInfo {

    private CoinType coinType;
    private double rsi;
}
