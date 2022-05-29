package com.dingdongdeng.coinautotrading.trading.exchange.future.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeTradingInfoParam;

public interface FutureExchangeService {

    ExchangeOrder order(ExchangeOrderParam param, String keyPairId);

    ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param, String keyPairId);

    ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param, String keyPairId);

    ExchangeOrder getOrderInfo(ExchangeOrderInfoParam param, String keyPairId);

    CoinExchangeType getCoinExchangeType();
}
