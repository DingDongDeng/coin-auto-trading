package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeLeverageParam {

    private CoinType coinType;
    private int leverage;
}
