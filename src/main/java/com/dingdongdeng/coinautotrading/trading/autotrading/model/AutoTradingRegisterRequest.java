package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.Map;
import javax.validation.constraints.NotNull;
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
public class AutoTradingRegisterRequest {

    @NotNull
    private String title;
    @NotNull
    private CoinType coinType;
    @NotNull
    private CoinExchangeType coinExchangeType;
    @NotNull
    private StrategyCode strategyCode;
    @NotNull
    private TradingTerm tradingTerm;
    @NotNull
    private String keyPairId;

    private Map<String, Object> strategyCoreParamMap;
}
