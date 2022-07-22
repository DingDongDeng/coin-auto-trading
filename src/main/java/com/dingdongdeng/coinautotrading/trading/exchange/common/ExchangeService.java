package com.dingdongdeng.coinautotrading.trading.exchange.common;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.MarketType;

public interface ExchangeService {

    CoinExchangeType getCoinExchangeType();

    MarketType getMarketType();
}
