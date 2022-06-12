package com.dingdongdeng.coinautotrading.trading.exchange.spot.service;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfoParam;

public interface SpotExchangeService extends ExchangeService {

    SpotExchangeOrder order(SpotExchangeOrderParam param, String keyPairId);

    SpotExchangeOrderCancel orderCancel(SpotExchangeOrderCancelParam param, String keyPairId);

    SpotExchangeTradingInfo getTradingInformation(SpotExchangeTradingInfoParam param, String keyPairId);

    SpotExchangeOrder getOrderInfo(SpotExchangeOrderInfoParam param, String keyPairId);

    @Override
    default MarketType getMarketType() {
        return MarketType.SPOT;
    }
}
