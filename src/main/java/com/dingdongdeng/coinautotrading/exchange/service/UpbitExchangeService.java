package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.State;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.TickerRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.TickerResponse;
import com.dingdongdeng.coinautotrading.exchange.component.IndexCalculator;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTicker;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfoParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpbitExchangeService implements ExchangeService {

    private final UpbitClient upbitClient;
    private final IndexCalculator indexCalculator;

    @Override
    public ExchangeOrder order(ExchangeOrderParam param) {
        log.info("upbit process : order param = {}", param);
        OrderResponse response = upbitClient.order(
            OrderRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .volume(param.getVolume())
                .price(param.getPrice())
                .ordType(OrdType.of(param.getPriceType()))
                .build()
        );
        return makeExchangeOrder(response);
    }

    @Override
    public ExchangeOrderCancel orderCancel(ExchangeOrderCancelParam param) {
        log.info("upbit process : order cancel param = {}", param);
        OrderCancelResponse response = upbitClient.orderCancel(
            OrderCancelRequest.builder()
                .uuid(param.getOrderId())
                .build()
        );
        return ExchangeOrderCancel.builder()
            .orderId(response.getUuid())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getOrdType().getPriceType())
            .price(response.getPrice())
            .state(response.getState())
            .market(response.getMarket())
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
    public ExchangeTradingInfo getTradingInformation(ExchangeTradingInfoParam param) {
        log.info("upbit process : get trading information param = {}", param);

        // 미체결 주문내역 조회
        List<ExchangeOrder> undecidedExchangeOrderList = getUndecidedOrderList(param);

        // 캔들 정보 조회
        ExchangeCandles candles = getExchangeCandles(param);

        // 현재가 정보 조회
        ExchangeTicker ticker = getExchangeTicker(param);

        // 계좌 정보 조회
        AccountsResponse accounts = upbitClient.getAccounts().stream().findFirst().orElseThrow(() -> new NoSuchElementException("계좌를 찾지 못함"));

        return ExchangeTradingInfo.builder()
            .currency(accounts.getCurrency())
            .balance(accounts.getBalance())
            .locked(accounts.getLocked())

            .avgBuyPrice(accounts.getAvgBuyPrice())
            .avgBuyPriceModified(accounts.isAvgBuyPriceModified())
            .unitCurrency(accounts.getUnitCurrency())

            .undecidedExchangeOrderList(undecidedExchangeOrderList)
            .candles(candles)
            .ticker(ticker)

            .rsi(indexCalculator.getRsi(candles, ticker.getTradePrice()))
            .build();
    }

    @Override
    public CoinExchangeType getExchangeType() {
        return CoinExchangeType.UPBIT;
    }

    private List<ExchangeOrder> getUndecidedOrderList(ExchangeTradingInfoParam param) {
        return upbitClient.getOrderInfoList(
            OrderInfoListRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .stateList(List.of(State.wait, State.watch))
                .build())
            .stream()
            .map(this::makeExchangeOrder)
            .collect(Collectors.toList());
    }

    private ExchangeCandles getExchangeCandles(ExchangeTradingInfoParam param) {
        TradingTerm tradingTerm = param.getTradingTerm();
        List<CandleResponse> response = upbitClient.getMinuteCandle(
            CandleRequest.builder()
                .unit(tradingTerm.getCandleUnit().getSize())
                .market(MarketType.of(param.getCoinType()).getCode())
                .toKst(LocalDateTime.now())
                .count(200)
                .build()
        );
        return ExchangeCandles.builder()
            .coinExchangeType(getExchangeType())
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
            .market(response.getMarket())
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

    private ExchangeTicker getExchangeTicker(ExchangeTradingInfoParam param) {
        TickerResponse response = upbitClient.getTicker(
            TickerRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .build()
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
