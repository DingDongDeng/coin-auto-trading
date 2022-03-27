package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContext;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTicker;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfoParam;
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
public class BackTestingExchangeService implements ExchangeService {

    private BackTestingContextLoader contextLoader;
    private ExchangeCandleService exchangeCandleService;
    private IndexCalculator indexCalculator;
    @Default
    private Map<String, ExchangeOrder> orderMap = new HashMap<>();

    @Override
    public ExchangeOrder order(ExchangeOrderParam param, String keyPairId) {
        ExchangeOrder order = ExchangeOrder.builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(param.getOrderType())
            .priceType(param.getPriceType())
            .price(param.getPrice())
            .avgPrice(null)
            .orderState(OrderState.DONE)
            .coinType(param.getCoinType())
            .createdAt(TradingTimeContext.now())
            .volume(param.getVolume())
            .remainingVolume(null)
            .reservedFee(null)
            .remainingFee(null)
            .paidFee(null)
            .locked(null)
            .executedVolume(null)
            .tradeCount(null)
            .tradeList(null)
            .build();

        orderMap.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param, String keyPairId) {
        ExchangeOrder order = orderMap.get(param.getOrderId());

        ExchangeOrderCancel orderCancel = ExchangeOrderCancel.builder()
            .orderId(param.getOrderId())
            .orderType(order.getOrderType())
            .priceType(order.getPriceType())
            .price(order.getPrice())
            .orderState(OrderState.CANCEL)
            .coinType(order.getCoinType())
            .createdAt(order.getCreatedAt())
            .volume(order.getVolume())
            .remainingVolume(order.getRemainingVolume())
            .reservedFee(order.getReservedFee())
            .remainingFee(order.getRemainingFee())
            .paidFee(order.getPaidFee())
            .locked(order.getLocked())
            .executedVolume(order.getExecutedVolume())
            .tradeCount(order.getTradeCount())
            .build();

        ExchangeOrder order1 = ExchangeOrder.builder()
            .orderId(param.getOrderId())
            .orderType(order.getOrderType())
            .priceType(order.getPriceType())
            .price(order.getPrice())
            .avgPrice(order.getAvgPrice())
            .orderState(OrderState.CANCEL)
            .coinType(order.getCoinType())
            .createdAt(order.getCreatedAt())
            .volume(order.getVolume())
            .remainingVolume(order.getRemainingVolume())
            .reservedFee(order.getReservedFee())
            .remainingFee(order.getRemainingFee())
            .paidFee(order.getPaidFee())
            .locked(order.getLocked())
            .executedVolume(order.getExecutedVolume())
            .tradeCount(order.getTradeCount())
            .tradeList(order.getTradeList())
            .build();

        orderMap.put(order1.getOrderId(), order1);
        return orderCancel;
    }

    @Override
    public ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param, String keyPairId) {
        BackTestingContext context = contextLoader.getCurrentContext();
        double currentPrice = context.getCurrentPrice();
        ExchangeCandles candles = context.getCandles();

        return ExchangeTradingInfo.builder()
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
            .ticker(ExchangeTicker.builder().tradePrice(currentPrice).build())

            .rsi(indexCalculator.getRsi(candles))
            .build();
    }

    @Override
    public ExchangeOrder getOrderInfo(ExchangeOrderInfoParam param, String keyPairId) {
        return orderMap.get(param.getOrderId());
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }


}
