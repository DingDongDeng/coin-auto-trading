package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContext;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.SpotExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTicker;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfoParam;
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
public class BackTestingExchangeService implements SpotExchangeService {

    private BackTestingContextLoader contextLoader;
    private IndexCalculator indexCalculator;
    private double exchangeFeeRate;
    @Default
    private Map<String, SpotExchangeOrder> orderMap = new HashMap<>();

    @Override
    public SpotExchangeOrder order(SpotExchangeOrderParam param, String keyPairId) {
        SpotExchangeOrder order = SpotExchangeOrder.builder()
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
            .remainingFee(0d)
            .paidFee(param.getVolume() * param.getPrice() * exchangeFeeRate / 100)
            .locked(null)
            .executedVolume(null)
            .tradeCount(null)
            .tradeList(null)
            .build();

        orderMap.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public SpotExchangeOrderCancel orderCancel(SpotExchangeOrderCancelParam param, String keyPairId) {
        SpotExchangeOrder order = orderMap.get(param.getOrderId());

        SpotExchangeOrderCancel orderCancel = SpotExchangeOrderCancel.builder()
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

        SpotExchangeOrder order1 = SpotExchangeOrder.builder()
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
    public SpotExchangeTradingInfo getTradingInformation(SpotExchangeTradingInfoParam param, String keyPairId) {
        BackTestingContext context = contextLoader.getCurrentContext();
        double currentPrice = context.getCurrentPrice();
        SpotExchangeCandles candles = context.getCandles();

        return SpotExchangeTradingInfo.builder()
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
            .ticker(SpotExchangeTicker.builder().tradePrice(currentPrice).build())

            .rsi(indexCalculator.getRsi(candles))
            .build();
    }

    @Override
    public SpotExchangeOrder getOrderInfo(SpotExchangeOrderInfoParam param, String keyPairId) {
        return orderMap.get(param.getOrderId());
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }


}
