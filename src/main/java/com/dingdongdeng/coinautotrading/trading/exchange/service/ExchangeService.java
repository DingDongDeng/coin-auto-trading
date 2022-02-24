package com.dingdongdeng.coinautotrading.trading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfoParam;

public interface ExchangeService {

    ExchangeOrder order(ExchangeOrderParam param, String keyPairId);

    ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param, String keyPairId);

    ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param, String keyPairId);

    ExchangeOrder getOrderInfo(ExchangeOrderInfoParam param, String keyPairId);

    CoinExchangeType getExchangeType();
}
