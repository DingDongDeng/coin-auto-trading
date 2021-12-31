package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;

public interface ExchangeProcessor {

    void process();

    CoinExchangeType getExchangeType();
}
