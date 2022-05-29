package com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Symbol;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureNewOrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Type;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureNewOrderResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BinanceFutureExchangeService implements ExchangeService {

    private final BinanceFutureClient binanceFutureClient;
    private final IndexCalculator indexCalculator;

    @Override
    public ExchangeOrder order(ExchangeOrderParam param, String keyPairId) {
        log.info("binance process : order param = {}", param);
        FutureNewOrderResponse response = binanceFutureClient.order(
            FutureNewOrderRequest.builder()
                .symbol(Symbol.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .quantity(param.getVolume())
                .price(param.getPrice())
                .type(Type.of(param.getPriceType()))
                .timeInForce()
                .timestamp()
                .build(),
            keyPairId
        );
        return makeExchangeOrder(response);
    }

    @Override
    public ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param, String keyPairId) {
        log.info("binance process : order cancel param = {}", param);
        OrderCancelResponse response = upbitClient.orderCancel(
            OrderCancelRequest.builder()
                .uuid(param.getOrderId())
                .build(),
            keyPairId
        );
        return ExchangeOrderCancel.builder()
            .orderId(response.getUuid())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getOrdType().getPriceType())
            .price(response.getPrice())
            .orderState(response.getState().getOrderState())
            .coinType(MarketType.of(response.getMarket()).getCoinType())
            .createdAt(response.getCreatedAt())
            .volume(response.getVolume())
            .remainingVolume(response.getRemainingVolume())
            .reservedFee(response.getReservedFee())
            .remainingFee(response.getRemainingFee())
            .paidFee(response.getPaidFee())
            .locked(response.getLocked())
            .executedVolume(response.getExecutedVolume())
            .tradeCount(response.getTradeCount())
            .build();
    }

    @Override
    public ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param, String keyPairId) {
        log.info("binance process : get trading information param = {}", param);

        // 캔들 정보 조회
        ExchangeCandles candles = getExchangeCandles(param, keyPairId);

        // 현재가 정보 조회
        ExchangeTicker ticker = getExchangeTicker(param, keyPairId);

        // 계좌 정보 조회
        AccountsResponse accounts = upbitClient.getAccounts(keyPairId).stream().findFirst().orElseThrow(() -> new NoSuchElementException("계좌를 찾지 못함"));

        return ExchangeTradingInfo.builder()
            .coinType(param.getCoinType())
            .coinExchangeType(getCoinExchangeType())
            .tradingTerm(param.getTradingTerm())
            .currency(accounts.getCurrency())
            .balance(accounts.getBalance())
            .locked(accounts.getLocked())

            .avgBuyPrice(accounts.getAvgBuyPrice())
            .avgBuyPriceModified(accounts.isAvgBuyPriceModified())
            .unitCurrency(accounts.getUnitCurrency())

            .candles(candles)
            .ticker(ticker)

            .rsi(indexCalculator.getRsi(candles))
            .build();
    }

    @Override
    public ExchangeOrder getOrderInfo(ExchangeOrderInfoParam param, String keyPairId) {
        return makeExchangeOrder(
            upbitClient.getOrderInfo(OrderInfoRequest.builder()
                    .uuid(param.getOrderId())
                    .build(),
                keyPairId
            )
        );
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return CoinExchangeType.BINANCE_FUTURE;
    }

    private ExchangeCandles getExchangeCandles(ExchangeTradingInfoParam param, String keyPairId) {
        TradingTerm tradingTerm = param.getTradingTerm();
        List<CandleResponse> response = upbitClient.getMinuteCandle(
            CandleRequest.builder()
                .unit(tradingTerm.getCandleUnit().getSize())
                .market(MarketType.of(param.getCoinType()).getCode())
                .toKst(TradingTimeContext.now())
                .count(60)
                .build(),
            keyPairId
        );
        Collections.reverse(response);
        return ExchangeCandles.builder()
            .coinExchangeType(getCoinExchangeType())
            .candleUnit(tradingTerm.getCandleUnit())
            .coinType(param.getCoinType())
            .candleList(
                response.stream().map(
                    candle -> ExchangeCandles.Candle.builder()
                        .candleDateTimeUtc(candle.getCandleDateTimeUtc())
                        .candleDateTimeKst(candle.getCandleDateTimeKst())
                        .openingPrice(candle.getOpeningPrice())
                        .highPrice(candle.getHighPrice())
                        .lowPrice(candle.getLowPrice())
                        .tradePrice(candle.getTradePrice())
                        .timestamp(candle.getTimestamp())
                        .candleAccTradePrice(candle.getCandleAccTradePrice())
                        .candleAccTradeVolume(candle.getCandleAccTradeVolume())
                        .build()
                ).collect(Collectors.toList())
            )
            .build();
    }

    private ExchangeOrder makeExchangeOrder(OrderResponse response) {
        return ExchangeOrder.builder()
            .orderId(response.getUuid())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getOrdType().getPriceType())
            .price(response.getPrice())
            .avgPrice(response.getAvgPrice())
            .orderState(response.getState().getOrderState())
            .coinType(MarketType.of(response.getMarket()).getCoinType())
            .createdAt(response.getCreatedAt())
            .volume(response.getVolume())
            .remainingVolume(response.getRemainingVolume())
            .reservedFee(response.getReservedFee())
            .remainingFee(response.getRemainingFee())
            .paidFee(response.getPaidFee())
            .locked(response.getLocked())
            .executedVolume(response.getExecutedVolume())
            .tradeCount(response.getTradeCount())
            .tradeList(response.getTradeList())
            .build();
    }

    private ExchangeTicker getExchangeTicker(ExchangeTradingInfoParam param, String keyPairId) {
        TickerResponse response = upbitClient.getTicker(
            TickerRequest.builder()
                .marketList(List.of(MarketType.of(param.getCoinType()).getCode()))
                .build()
            , keyPairId
        ).stream().findFirst().orElseThrow(NoSuchElementException::new);
        return ExchangeTicker.builder()
            .market(response.getMarket())
            .tradeDate(response.getTradeDate())
            .tradeTime(response.getTradeTime())
            .tradeDateKst(response.getTradeDateKst())
            .tradeTimeKst(response.getTradeTimeKst())
            .openingPrice(response.getOpeningPrice())
            .highPrice(response.getHighPrice())
            .lowPrice(response.getLowPrice())
            .tradePrice(response.getTradePrice())
            .prevClosingPrice(response.getPrevClosingPrice())
            .change(response.getChange())
            .changePrice(response.getChangePrice())
            .changeRate(response.getChangeRate())
            .signedChangePrice(response.getSignedChangePrice())
            .signedChangeRate(response.getSignedChangeRate())
            .tradeVolume(response.getTradeVolume())
            .accTradePrice(response.getAccTradePrice())
            .accTradePrice24h(response.getAccTradePrice24h())
            .accTradeVolume(response.getAccTradeVolume())
            .accTradeVolume24h(response.getAccTradeVolume24h())
            .highest52WeekPrice(response.getHighest52WeekPrice())
            .highest52WeekDate(response.getHighest52WeekDate())
            .lowest52WeekPrice(response.getLowest52WeekPrice())
            .lowest52WeekDate(response.getLowest52WeekDate())
            .timestamp(response.getTimestamp())
            .build();
    }
}
