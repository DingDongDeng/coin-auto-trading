package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfoParam;

public interface ExchangeService {

    ExchangeOrder order(ExchangeOrderParam param);

    ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param);

    ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param);

    CoinExchangeType getExchangeType();
}
