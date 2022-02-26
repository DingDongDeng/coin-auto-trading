package com.dingdongdeng.coinautotrading.trading.exchange.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExchangeOrderCancelParam {

    private String orderId;
}
