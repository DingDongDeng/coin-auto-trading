package com.dingdongdeng.coinautotrading.trading.exchange.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExchangeTradingInfoParam {

    private CoinType coinType;
    private TradingTerm tradingTerm;
    private LocalDateTime now;
}
