package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContext;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTicker;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.index.IndexCalculator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
        FutureExchangeOrder order = FutureExchangeOrder.builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(param.getOrderType())
            .priceType(param.getPriceType())
            .price(param.getPrice())
            .avgPrice(null)
            .orderState(OrderState.DONE)
            .coinType(param.getCoinType())
            .createdAt(TradingTimeContext.now())
            .volume(param.getVolume())
            .executedVolume(null)
            .cumQuote(null)
            .build();

        orderMap.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public FutureExchangeOrderCancel orderCancel(FutureExchangeOrderCancelParam param,
        String keyPairId) {
        FutureExchangeOrder order = orderMap.get(param.getOrderId());

        FutureExchangeOrderCancel orderCancel = FutureExchangeOrderCancel.builder()
            .orderId(param.getOrderId())
            .orderType(order.getOrderType())
            .priceType(order.getPriceType())
            .price(order.getPrice())
            .orderState(order.getOrderState())
            .coinType(order.getCoinType())
            .createdAt(order.getCreatedAt())
            .volume(order.getVolume())
            .executedVolume(order.getExecutedVolume())
            .cumQuote(order.getCumQuote())
            .build();

        FutureExchangeOrder order1 = FutureExchangeOrder.builder()
            .orderId(param.getOrderId())
            .orderType(order.getOrderType())
            .priceType(order.getPriceType())
            .price(order.getPrice())
            .avgPrice(order.getAvgPrice())
            .orderState(OrderState.CANCEL)
            .coinType(order.getCoinType())
            .createdAt(order.getCreatedAt())
            .volume(order.getVolume())
            .executedVolume(order.getExecutedVolume())
            .cumQuote(order.getCumQuote())
            .build();

        orderMap.put(order1.getOrderId(), order1);
        return orderCancel;
    }

    @Override
    public FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId) {
        BackTestingContext context = contextLoader.getCurrentContext();
        double currentPrice = context.getCurrentPrice();
        ExchangeCandles candles = context.getCandles();

        return FutureExchangeTradingInfo.builder()
            .coinType(param.getCoinType())
            .coinExchangeType(getCoinExchangeType())
            .tradingTerm(param.getTradingTerm())
            .currency(null)
            .balance(400 * 10000d)
            .locked(null)

            .avgBuyPrice(null)
            .avgBuyPriceModified(false)
            .unitCurrency(null)

            .candles(candles)
            .ticker(FutureExchangeTicker.builder().close(currentPrice).build())

            .rsi(indexCalculator.getRsi(candles))
            .build();
    }

    @Override
    public FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId) {
        return orderMap.get(param.getOrderId());
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }
}
