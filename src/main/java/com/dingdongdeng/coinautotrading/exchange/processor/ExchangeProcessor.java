package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultAccount;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrderInfo;

public interface ExchangeProcessor {

    ProcessResultOrder order(OrderType orderType);

    ProcessResultOrderCancel orderCancel();

    ProcessResultAccount getAccount();

    ProcessResultOrderInfo getOrderInfo();

    CoinExchangeType getExchangeType();
}
