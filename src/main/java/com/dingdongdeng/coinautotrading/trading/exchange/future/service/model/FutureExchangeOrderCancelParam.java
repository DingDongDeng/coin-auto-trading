package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeOrderCancelParam {

    private String symbol;
    private String orderId;

}
