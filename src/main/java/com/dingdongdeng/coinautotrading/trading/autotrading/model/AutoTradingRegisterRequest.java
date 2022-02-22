package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AutoTradingRegisterRequest {

    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
    private TradingTerm tradingTerm;
    private StrategyCode strategyCode;
}
