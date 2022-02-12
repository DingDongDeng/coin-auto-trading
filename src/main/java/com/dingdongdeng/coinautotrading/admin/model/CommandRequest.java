package com.dingdongdeng.coinautotrading.admin.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CommandRequest {

    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
    private TradingTerm tradingTerm;
    private StrategyCode strategyCode;
}
