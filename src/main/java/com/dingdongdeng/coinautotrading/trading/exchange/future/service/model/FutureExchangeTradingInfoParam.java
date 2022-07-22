package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeTradingInfoParam {

    private CoinType coinType;
    private TradingTerm tradingTerm;
}
