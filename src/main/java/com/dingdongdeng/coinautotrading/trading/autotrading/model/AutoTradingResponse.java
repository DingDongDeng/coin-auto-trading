package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingStatus;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoTradingResponse {

    private String title;
    private String processorId;
    private long processDuration;
    private AutoTradingStatus autoTradingStatus;
    private String userId;
    private StrategyCode strategyCode;
    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
}
