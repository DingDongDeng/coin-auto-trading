package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.index.Index;

public interface TradingInfo {

    ExchangeCandles getCandles();

    Index getIndex();
}
