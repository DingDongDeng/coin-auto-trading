package com.dingdongdeng.coinautotrading.autotrading.model;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class AutoTradingStartParam {

    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
    private TradingTerm tradingTerm;
    private StrategyCode strategyCode;
}
