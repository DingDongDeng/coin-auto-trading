package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContext;
import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Builder
public class BackTestingSpotExchangeService implements SpotExchangeService {

    private BackTestingContextLoader contextLoader;
    private IndexCalculator indexCalculator;
    private double exchangeFeeRate;
    @Default
    private Map<String, SpotExchangeOrder> orderMap = new HashMap<>();

    // 누적 거래량 계산을 위한 필드
    private LocalDateTime snapshotCandleDateTime;
    private double snapshotCandleAccTradeVolume;
    private double highPrice;
    private double lowPrice = Double.MAX_VALUE;

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
        Candle currentCandle = context.getCurrentCandle();
        ExchangeCandles candles = this.deepCopyCandles(context.getCandles());

        // 캔들 정보에 현재 정보 추가 (누적 거래량 계산을 위한 로직)
        Candle lastCandle = candles.getCandleList().get(candles.getCandleList().size() - 1);
        if (Objects.nonNull(this.snapshotCandleDateTime) && lastCandle.getCandleDateTimeKst().isEqual(this.snapshotCandleDateTime)) {
            this.snapshotCandleAccTradeVolume += currentCandle.getCandleAccTradeVolume();
            this.highPrice = Math.max(this.highPrice, currentPrice);
            this.lowPrice = Math.min(this.lowPrice, currentPrice);
        } else {
            this.snapshotCandleDateTime = lastCandle.getCandleDateTimeKst();
            this.snapshotCandleAccTradeVolume = currentCandle.getCandleAccTradeVolume();
            this.highPrice = currentCandle.getHighPrice();
            this.lowPrice = currentCandle.getLowPrice();
        }
        candles.getCandleList().remove(lastCandle);
        candles.getCandleList().add(
            Candle.builder()
                .candleDateTimeUtc(currentCandle.getCandleDateTimeUtc())
                .candleDateTimeKst(currentCandle.getCandleDateTimeKst())
                .openingPrice(lastCandle.getOpeningPrice())
                .highPrice(this.highPrice)
                .lowPrice(this.lowPrice)
                .tradePrice(currentCandle.getTradePrice())
                .timestamp(currentCandle.getTimestamp())
                .candleAccTradePrice(null)
                .candleAccTradeVolume(this.snapshotCandleAccTradeVolume)
                .build()
        );

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
            .ticker(SpotExchangeTicker.builder()
                .tradePrice(currentPrice)
                .build())

            .index(indexCalculator.getIndex(candles))
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

    private ExchangeCandles deepCopyCandles(ExchangeCandles candles) {
        return ExchangeCandles.builder()
            .coinExchangeType(candles.getCoinExchangeType())
            .coinType(candles.getCoinType())
            .candleUnit(candles.getCandleUnit())
            .candleList(new ArrayList<>(candles.getCandleList()))
            .build();
    }

}
