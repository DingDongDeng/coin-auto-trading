package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContext;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeLeverage;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeLeverageParam;
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
    private int leverage;
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
            .volume(param.getVolume() * leverage)   //fixme 첫 포지션때만 레버리지가 곱해지도록(매 주문마다 곱해버리면 안됨)
            .executedVolume(null)
            .cumQuote(null)
            .paidFee(getCancelFee(param.getVolume(), param.getPrice()))
            .build();

        orderMap.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public FutureExchangeOrderCancel orderCancel(FutureExchangeOrderCancelParam param, String keyPairId) {
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
            .paidFee(getCancelFee(order.getVolume(), order.getPrice()))
            .build();

        orderMap.put(order1.getOrderId(), order1);
        return orderCancel;
    }

    @Override
    public FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId) {
        /*
        FIXME
            if(currentPrice <= 계산된 청산가){
                orderList를 전부다 OrderState.LIQUIDATION으로 수정
            }
         */

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
            .ticker(FutureExchangeTicker.builder().markPrice(currentPrice).build())

            .leverage(leverage)
            .liquidationPrice(getLiquidationPrice())

            .rsi(indexCalculator.getRsi(candles))
            .resistancePriceList(indexCalculator.getResistancePrice(candles))
            .build();
    }

    @Override
    public FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId) {
        return orderMap.get(param.getOrderId());
    }

    @Override
    public FutureExchangeLeverage updateLeverage(FutureExchangeLeverageParam param, String keyPairId) {
        return null;
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }

    private double getCancelFee(double volume, double price) {
        return exchangeFeeRate * volume * price / 100;
    }

    private double getLiquidationPrice() {
        return 0d; //fixme 이것도
    }
}
