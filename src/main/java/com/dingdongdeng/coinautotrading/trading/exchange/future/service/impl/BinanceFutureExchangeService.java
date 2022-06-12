package com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Interval;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Symbol;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.TimeInForce;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Type;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureAccountBalanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureCandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureMarkPriceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureNewOrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureOrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureAccountBalanceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureCandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureMarkPriceResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureNewOrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderCancelResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureOrderInfoResponse;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;
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
                .symbol(Symbol.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .type(Type.of(param.getPriceType()))
                .price(param.getPrice())
                .quantity(param.getVolume())
                .timeInForce(TimeInForce.GTC)
                .timestamp(System.currentTimeMillis())
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
                .symbol(Symbol.of(param.getSymbol()).getCode())
                .orderId(param.getOrderId())
                .timestamp(System.currentTimeMillis())
                .build(),
            keyPairId
        );
        return FutureExchangeOrderCancel.builder()
            .cumQty(response.getCumQty())
            .cumQuote(response.getCumQuote())
            .executedQty(response.getExecutedQty())
            .orderId(response.getOrderId().toString())
            .origQty(response.getOrigQty())
            .origType(response.getOrigType())
            .price(response.getPrice())
            .reduceOnly(response.getReduceOnly())
            .orderType(response.getSide().getOrderType())
            .positionSide(response.getPositionSide().getPosition())
            .orderState(response.getStatus().getOrderState())
            .stopPrice(response.getStopPrice())
            .closePosition(response.getClosePosition())
            .coinType(Symbol.of(response.getSymbol()).getCoinType())
            .timeInForceType(response.getTimeInForce().getTimeInForceType())
            .priceType(response.getType().getPriceType())
            .activatePrice(response.getActivatePrice())
            .priceRate(response.getPriceRate())
            .updateTime(convertTime(response.getUpdateTime()))
            .workingType(response.getWorkingType())
            .priceProtect(response.getPriceProtect())
            .build();
    }

    @Override
    public FutureExchangeTradingInfo getTradingInformation(FutureExchangeTradingInfoParam param, String keyPairId) {
        log.info("binance process : get trading information param = {}", param);


        // 캔들 정보 조회
        ExchangeCandles candles = getExchangeCandles(param);

        // 현재가 정보 조회
        FutureExchangeTicker ticker = getExchangeTicker(param);

        // 계좌 정보 조회
        FutureAccountBalanceResponse accounts = getAssetBalance(keyPairId);

        return FutureExchangeTradingInfo.builder()
            .coinType(param.getCoinType())
            .coinExchangeType(getCoinExchangeType())
            .tradingTerm(param.getTradingTerm())
            .currency(accounts.getAsset())
            .balance(accounts.getBalance())

            .candles(candles)
            .ticker(ticker)

            .rsi(indexCalculator.getRsi(candles))   //fixme 파라미터가 spot만 가능한데 선물도 가능하게좀
            .build();
    }

    @Override
    public FutureExchangeOrder getOrderInfo(FutureExchangeOrderInfoParam param, String keyPairId) {
        return makeExchangeOrder(
            binanceFutureClient.getFutureOrderInfo(FutureOrderInfoRequest.builder()
                    .symbol(param.getSymbol())
                    .orderId(param.getOrderId())
                    .timestamp(System.currentTimeMillis())
                    .build()
                , keyPairId
            )
        );
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return CoinExchangeType.BINANCE_FUTURE;
    }

    private ExchangeCandles getExchangeCandles(FutureExchangeTradingInfoParam param) {
        TradingTerm tradingTerm = param.getTradingTerm();
        List<FutureCandleResponse> response = binanceFutureClient.getMinuteCandle(
            FutureCandleRequest.builder()
                .symbol(Symbol.of(param.getCoinType()).getCode())
                .interval(Interval.of(tradingTerm.getCandleUnit()).getCode())
                .limit(60)
                .build()
        );
        Collections.reverse(response);
        return ExchangeCandles.builder()
            .coinExchangeType(getCoinExchangeType())
            .candleUnit(tradingTerm.getCandleUnit())
            .coinType(param.getCoinType())
            .candleList(
                response.stream().map(
                    candle -> ExchangeCandles.Candle.builder()
                        .candleDateTimeUtc(convertTime(candle.getCloseTime()))
                        .candleDateTimeKst(convertTime(candle.getCloseTime()))
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
            .orderId(response.getOrderId().toString())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getType().getPriceType())
            .price(response.getPrice())
            .avgPrice(response.getAvgPrice())
            .orderState(response.getStatus().getOrderState())
            .coinType(Symbol.of(response.getSymbol()).getCoinType())
            .createdAt(convertTime(response.getUpdateTime()))
            .volume(response.getOrigQty())
            .executedVolume(response.getExecutedQty())
            .build();
    }

    private FutureExchangeOrder makeExchangeOrder(FutureOrderInfoResponse response) {
        return FutureExchangeOrder.builder()
            .orderId(response.getOrderId().toString())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getType().getPriceType())
            .price(response.getPrice())
            .avgPrice(response.getAvgPrice())
            .orderState(response.getStatus().getOrderState())
            .coinType(Symbol.of(response.getSymbol()).getCoinType())
            .createdAt(convertTime(response.getUpdateTime()))
            .volume(response.getOrigQty())
            .executedVolume(response.getExecutedQty())
            .build();
    }

    private FutureExchangeTicker getExchangeTicker(FutureExchangeTradingInfoParam param) {
         FutureMarkPriceResponse response = binanceFutureClient.getMarkPrice(
            FutureMarkPriceRequest.builder()
                .symbol(Symbol.of(param.getCoinType()).getCode())
                .build()
        );
        return FutureExchangeTicker.builder()
                .symbol(response.getSymbol())
                .markPrice(response.getMarkPrice())
                .indexPrice(response.getIndexPrice())
                .estimatedSettlePrice(response.getEstimatedSettlePrice())
                .lastFundingRate(response.getLastFundingRate())
                .interestRate(response.getInterestRate())
                .nextFundingTime(response.getNextFundingTime())
                .build();
    }

    private FutureAccountBalanceResponse getAssetBalance(String keyPairId){
        return binanceFutureClient.getFuturesAccountBalance(
                FutureAccountBalanceRequest.builder()
                    .timestamp(System.currentTimeMillis())
                    .build()
                ,keyPairId
            ).stream()
            .filter(m -> m.getAsset().equals("USDT"))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("남아있는 USDT를 찾지 못함"));
    }

    private LocalDateTime convertTime(Long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }
}
