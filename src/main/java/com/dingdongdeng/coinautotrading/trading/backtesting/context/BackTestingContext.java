package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BackTestingContext {

    private CoinExchangeType coinExchangeType;
    private CoinType coinType;
    private Double currentPrice;
    private LocalDateTime now;
    private Double balance;
}
