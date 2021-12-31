package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;

public interface ExchangeProcessor {

    void order();

    void orderCancel();

    void getAccount();

    CoinExchangeType getExchangeType();
}
