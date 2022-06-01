package com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Interval;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Symbol;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureCandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureNewOrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Type;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureCandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureNewOrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderInfoResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTicker;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.MarketType;
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
public class BinanceFutureExchangeService implements FutureExchangeService {

    private final BinanceFutureClient binanceFutureClient;
    private final IndexCalculator indexCalculator;

    @Override
    public FutureExchangeOrder order(FutureExchangeOrderParam param, String keyPairId) {
        log.info("binance process : order param = {}", param);
        FutureNewOrderResponse response = binanceFutureClient.order(
            FutureNewOrderRequest.builder()
                .build(),
            keyPairId
        );
        return makeExchangeOrder(response);
    }

    @Override
    public FutureExchangeOrderCancel orderCancel(FutureExchangeOrderCancelParam param, String keyPairId) {
        log.info("binance process : order cancel param = {}", param);
        FutureOrderCancelResponse response = binanceFutureClient.orderCancel(
            FutureOrderCancelRequest.builder()
                .build(),
            keyPairId
        );
        return FutureExchangeOrderCancel.builder()
            .build();
    }

    @Override
    public FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId) {
        log.info("binance process : get trading information param = {}", param);

        // 캔들 정보 조회
        FutureExchangeCandles candles = getExchangeCandles(param, keyPairId);

        // 현재가 정보 조회
        FutureExchangeTicker ticker = getExchangeTicker(param, keyPairId);

        // 계좌 정보 조회
        // fixme 선물이라면 현재 내가 들고 있는 포지션?
        FutureOrderInfoResponse accounts = binanceFutureClient.getFutureOrderInfo(
            FutureOrderInfoRequest.builder()
                .build()
            ,keyPairId
        );

        return FutureExchangeTradingInfo.builder()
            .build();
    }

    @Override
    public FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId) {
        return makeExchangeOrderInfo(
            binanceFutureClient.getFutureOrderInfo(FutureOrderInfoRequest.builder().build()
                , keyPairId
            )
        );
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return CoinExchangeType.BINANCE_FUTURE;
    }

    private FutureExchangeCandles getExchangeCandles(FutureExchangeTradingInfoParam param, String keyPairId) {
        TradingTerm tradingTerm = param.getTradingTerm();
        List<FutureCandleResponse> response = binanceFutureClient.getMinuteCandle(
            FutureCandleRequest.builder()
                .symbol(Symbol.of(param.getCoinType()).getCode())
                .interval(Interval.of(tradingTerm.getCandleUnit()).getCode())
                .build()
        );
        Collections.reverse(response);
        return FutureExchangeCandles.builder()
            .coinExchangeType(getCoinExchangeType())
            .candleUnit(tradingTerm.getCandleUnit())
            .coinType(param.getCoinType())
            .candleList(
                response.stream().map(
                    candle -> FutureExchangeCandles.Candle.builder()
                        .candleDateTimeUtc(candle.getCandleDateTimeUtc())
                        .candleDateTimeKst(candle.getCandleDateTimeKst())
                        .openingPrice(candle.getOpen())
                        .highPrice(candle.getHigh())
                        .lowPrice(candle.getLow())
                        .tradePrice(candle.getClose())
                        .timestamp(candle.getCloseTime())
                        .candleAccTradePrice(candle.getQuoteAssetVolume())
                        .candleAccTradeVolume(candle.getVolume())
                        .build()
                ).collect(Collectors.toList())
            )
            .build();
    }

    private FutureExchangeOrder makeExchangeOrder(FutureNewOrderResponse response) {
        return FutureExchangeOrder.builder()
            .build();
    }

    private FutureExchangeOrder makeExchangeOrderInfo(FutureOrderInfoResponse response) {
        return FutureExchangeOrder.builder()
            .build();
    }

    /*private FutureExchangeTicker getExchangeTicker(FutureExchangeTradingInfoParam param, String keyPairId) {
        TickerResponse response = upbitClient.getTicker(    //fixme 업비트의에서 ticker는 바이낸스의 https://binance-docs.github.io/apidocs/futures/en/#mark-price 이걸 참고하면 될듯
            TickerRequest.builder()
                .marketList(List.of(MarketType.of(param.getCoinType()).getCode()))
                .build()
            , keyPairId
        ).stream().findFirst().orElseThrow(NoSuchElementException::new);
        return FutureExchangeTicker.builder()
            .build();
    }*/
}
