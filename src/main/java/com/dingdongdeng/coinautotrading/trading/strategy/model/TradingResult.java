package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;

public interface TradingResult {

    String getIdentifyCode();

    String getOrderId();

    OrderType getOrderType();

    Double getPrice();

    Double getVolume();

    Double getFee();

    TradingTag getTradingTag();

    TradingTerm getTradingTerm();

    boolean isDone();
}