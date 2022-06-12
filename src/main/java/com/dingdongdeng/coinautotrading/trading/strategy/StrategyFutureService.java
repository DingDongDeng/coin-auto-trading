package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class StrategyFutureService implements StrategyService {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final String keyPairId;
    private final FutureExchangeService futureExchangeService;

    @Override
    public TradingInfo getTradingInformation(String identifyCode, TradingResultPack tradingResultPack) {
        FutureExchangeTradingInfoParam param = FutureExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();

        FutureExchangeTradingInfo futureExchangeTradingInfo = futureExchangeService.getTradingInformation(param, keyPairId);

        return TradingInfo.builder()
            .identifyCode(identifyCode)
            .coinExchangeType(futureExchangeTradingInfo.getCoinExchangeType())
            .coinType(futureExchangeTradingInfo.getCoinType())
            .tradingTerm(futureExchangeTradingInfo.getTradingTerm())
            .currency(futureExchangeTradingInfo.getCurrency())
            .candles(futureExchangeTradingInfo.getCandles())
            .balance(futureExchangeTradingInfo.getBalance())
            .locked(futureExchangeTradingInfo.getLocked())
            .avgBuyPrice(futureExchangeTradingInfo.getAvgBuyPrice())
            .unitCurrency(futureExchangeTradingInfo.getUnitCurrency())
            .currentPrice(futureExchangeTradingInfo.getTicker().getMarkPrice())
            .tradingResultPack(tradingResultPack)
            .rsi(futureExchangeTradingInfo.getRsi())
            .build();
    }

    @Override
    public TradingResult order(TradingTask orderTradingTask) {
        FutureExchangeOrderParam param = FutureExchangeOrderParam.builder()
            .coinType(orderTradingTask.getCoinType())
            .orderType(orderTradingTask.getOrderType())
            .volume(orderTradingTask.getVolume())
            .price(orderTradingTask.getPrice())
            .priceType(orderTradingTask.getPriceType())
            .build();
        FutureExchangeOrder futureExchangeOrder = futureExchangeService.order(param, keyPairId);
        return makeTradingResult(orderTradingTask, futureExchangeOrder);
    }

    @Override
    public TradingResult orderCancel(TradingTask cancelTradingTask) {
        FutureExchangeOrderCancelParam param = FutureExchangeOrderCancelParam.builder()
            .orderId(cancelTradingTask.getOrderId())
            .build();
        FutureExchangeOrderCancel futureExchangeOrderCancel = futureExchangeService.orderCancel(param, keyPairId);
        return makeTradingResult(cancelTradingTask, futureExchangeOrderCancel);
    }

    @Override
    public TradingResultPack updateTradingResultPack(TradingResultPack tradingResultPack) {
        return new TradingResultPack(
            updateTradingResultList(tradingResultPack.getBuyTradingResultList()),
            updateTradingResultList(tradingResultPack.getProfitTradingResultList()),
            updateTradingResultList(tradingResultPack.getLossTradingResultList())
        );
    }

    private List<TradingResult> updateTradingResultList(List<TradingResult> tradingResultList) {
        return tradingResultList.stream()
            .map(this::updateTradingResult)
            .collect(Collectors.toList());
    }

    private TradingResult updateTradingResult(TradingResult tradingResult) {
        FutureExchangeOrder futureExchangeOrder =
            futureExchangeService.getOrderInfo(FutureExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build(), keyPairId);
        return TradingResult.builder()
            .identifyCode(tradingResult.getIdentifyCode())
            .coinType(futureExchangeOrder.getCoinType())
            .tradingTerm(tradingResult.getTradingTerm())
            .orderType(futureExchangeOrder.getOrderType())
            .orderState(futureExchangeOrder.getOrderState())
            .volume(futureExchangeOrder.getVolume())
            .price(futureExchangeOrder.getPrice())
            .priceType(futureExchangeOrder.getPriceType())
            .orderId(futureExchangeOrder.getOrderId())
            .tag(tradingResult.getTag())
            .createdAt(futureExchangeOrder.getCreatedAt())
            .build();
    }

    private TradingResult makeTradingResult(TradingTask tradingTask, FutureExchangeOrder futureExchangeOrder) {
        return TradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(futureExchangeOrder.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(futureExchangeOrder.getPaidFee() + futureExchangeOrder.getRemainingFee())
            .priceType(tradingTask.getPriceType())
            .orderId(futureExchangeOrder.getOrderId())
            .tag(tradingTask.getTag())
            .createdAt(futureExchangeOrder.getCreatedAt())
            .build();
    }

    private TradingResult makeTradingResult(TradingTask tradingTask, FutureExchangeOrderCancel futureExchangeOrderCancel) { //fixme 중복 코드 개선
        return TradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(futureExchangeOrderCancel.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(futureExchangeOrderCancel.getPaidFee() + futureExchangeOrderCancel.getRemainingFee())
            .priceType(tradingTask.getPriceType())
            .orderId(futureExchangeOrderCancel.getOrderId())
            .tag(tradingTask.getTag())
            .createdAt(futureExchangeOrderCancel.getCreatedAt())
            .build();
    }

}
