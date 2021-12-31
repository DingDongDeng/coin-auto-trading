package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;

public interface ExchangeProcessor {

    void order(OrderType orderType);

    void orderCancel();

    void getAccount();

    CoinExchangeType getExchangeType();
}
