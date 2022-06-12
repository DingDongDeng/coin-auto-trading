package com.dingdongdeng.coinautotrading.trading.exchange.future.service;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;


public interface FutureExchangeService extends ExchangeService {

    FutureExchangeOrder order(FutureExchangeOrderParam param, String keyPairId);

    FutureExchangeOrderCancel orderCancel(FutureExchangeOrderCancelParam param, String keyPairId);

    FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId);

    FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId);

    @Override
    default MarketType getMarketType() {
        return MarketType.FUTURE;
    }
}
