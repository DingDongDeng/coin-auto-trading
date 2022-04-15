package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingProcessStatus;
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
    private AutoTradingProcessStatus processStatus;
    private String userId;
    private String strategyIdentifyCode;
    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
    private TradingTerm tradingTerm;

    // 전략 세부 설정 값
    private Double buyRsi;
    private Double profitRsi;
    private Double lossRsi;
    private Double profitLimitPriceRate;
    private Double lossLimitPriceRate;
    private Integer tooOldOrderTimeSeconds;
    private Double orderPrice;
    private Double accountBalanceLimit;
}
