package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.index.IndexCalculator;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Builder
public class BackTestingFutureExchangeService implements FutureExchangeService {

    private BackTestingContextLoader contextLoader;
    private IndexCalculator indexCalculator;
    private double exchangeFeeRate;
    @Default
    private Map<String, FutureExchangeOrder> orderMap = new HashMap<>();

    @Override
    public FutureExchangeOrder order(FutureExchangeOrderParam param, String keyPairId) {
        return null;
    }

    @Override
    public FutureExchangeOrderCancel orderCancel(FutureExchangeOrderCancelParam param, String keyPairId) {
        return null;
    }

    @Override
    public FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId) {
        return null;
    }

    @Override
    public FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId) {
        return null;
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }
}
