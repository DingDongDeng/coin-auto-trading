package com.dingdongdeng.coinautotrading.console.controller.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.EnumMap;
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
public class TypeInfoResponse {

    private EnumMap<CoinType, String> coinTypeMap;
    private EnumMap<CoinExchangeType, String> coinExchangeTypeMap;
    private EnumMap<TradingTerm, String> tradingTermMap;
    private EnumMap<StrategyCode, String> strategyCodeMap;
}
