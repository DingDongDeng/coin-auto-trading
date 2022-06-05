package com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.UpbitClient;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.TickerRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.TickerResponse;
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
public class UpbitSpotExchangeService implements SpotExchangeService {

    private final UpbitClient upbitClient;
    private final IndexCalculator indexCalculator;

    @Override
    public SpotExchangeOrder order(SpotExchangeOrderParam param, String keyPairId) {
        log.info("upbit process : order param = {}", param);
        OrderResponse response = upbitClient.order(
            OrderRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .volume(param.getVolume())
                .price(param.getPrice())
                .ordType(OrdType.of(param.getPriceType()))
                .build(),
            keyPairId
        );
        return makeExchangeOrder(response);
    }

    @Override
    public SpotExchangeOrderCancel orderCancel(SpotExchangeOrderCancelParam param, String keyPairId) {
        log.info("upbit process : order cancel param = {}", param);
        OrderCancelResponse response = upbitClient.orderCancel(
            OrderCancelRequest.builder()
                .uuid(param.getOrderId())
                .build(),
            keyPairId
        );
        return SpotExchangeOrderCancel.builder()
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
    public SpotExchangeTradingInfo getTradingInformation(SpotExchangeTradingInfoParam param, String keyPairId) {
        log.info("upbit process : get trading information param = {}", param);

        // 캔들 정보 조회
        ExchangeCandles candles = getExchangeCandles(param, keyPairId);

        // 현재가 정보 조회
        SpotExchangeTicker ticker = getExchangeTicker(param, keyPairId);

        // 계좌 정보 조회
        //fixme findFirst에 원화만 오는게 아니라 balance가 큰 순서인것 같은니 수정해야 될듯?
        AccountsResponse accounts = upbitClient.getAccounts(keyPairId).stream().findFirst().orElseThrow(() -> new NoSuchElementException("계좌를 찾지 못함"));

        return SpotExchangeTradingInfo.builder()
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
    public SpotExchangeOrder getOrderInfo(SpotExchangeOrderInfoParam param, String keyPairId) {
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
        return CoinExchangeType.UPBIT;
    }

    private ExchangeCandles getExchangeCandles(SpotExchangeTradingInfoParam param, String keyPairId) {
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

    private SpotExchangeOrder makeExchangeOrder(OrderResponse response) {
        return SpotExchangeOrder.builder()
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

    private SpotExchangeTicker getExchangeTicker(SpotExchangeTradingInfoParam param, String keyPairId) {
        TickerResponse response = upbitClient.getTicker(
            TickerRequest.builder()
                .marketList(List.of(MarketType.of(param.getCoinType()).getCode()))
                .build()
            , keyPairId
        ).stream().findFirst().orElseThrow(NoSuchElementException::new);
        return SpotExchangeTicker.builder()
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
