package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.FutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeLeverageParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.FutureTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreFutureParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
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
public class StrategyFutureService implements StrategyService<FutureTradingInfo, FutureTradingResult> {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final String keyPairId;
    private final FutureExchangeService futureExchangeService;

    @Override
    public void ready(StrategyCoreParam param) {
        StrategyCoreFutureParam futureParam = (StrategyCoreFutureParam) param;
        futureExchangeService.updateLeverage(FutureExchangeLeverageParam.builder()
                .coinType(coinType)
                .leverage(futureParam.getLeverage())
                .build(),
            keyPairId
        );
    }

    @Override
    public FutureTradingInfo getTradingInformation(String identifyCode) {
        FutureExchangeTradingInfoParam param = FutureExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();

        FutureExchangeTradingInfo futureExchangeTradingInfo = futureExchangeService.getTradingInformation(param, keyPairId);

        return FutureTradingInfo.builder()
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
            .liquidationPrice(futureExchangeTradingInfo.getLiquidationPrice())
            .leverage(futureExchangeTradingInfo.getLeverage())
            .rsi(futureExchangeTradingInfo.getRsi())
            .resistancePriceList(futureExchangeTradingInfo.getResistancePriceList())
            .build();
    }

    @Override
    public FutureTradingResult order(TradingTask orderTradingTask) {
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
    public FutureTradingResult orderCancel(TradingTask cancelTradingTask) {
        FutureExchangeOrderCancelParam param = FutureExchangeOrderCancelParam.builder()
            .orderId(cancelTradingTask.getOrderId())
            .build();
        FutureExchangeOrderCancel futureExchangeOrderCancel = futureExchangeService.orderCancel(param, keyPairId);
        return makeTradingResult(cancelTradingTask, futureExchangeOrderCancel);
    }

    @Override
    public TradingResultPack<FutureTradingResult> updateTradingResultPack(TradingResultPack<FutureTradingResult> tradingResultPack) {
        return new TradingResultPack<>(
            updateTradingResultList(tradingResultPack.getBuyTradingResultList()),
            updateTradingResultList(tradingResultPack.getProfitTradingResultList()),
            updateTradingResultList(tradingResultPack.getLossTradingResultList())
        );
    }

    private List<FutureTradingResult> updateTradingResultList(List<FutureTradingResult> tradingResultList) {
        return tradingResultList.stream()
            .map(this::updateTradingResult)
            .collect(Collectors.toList());
    }

    private FutureTradingResult updateTradingResult(FutureTradingResult tradingResult) {
        FutureExchangeOrder futureExchangeOrder =
            futureExchangeService.getOrderInfo(FutureExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build(), keyPairId);
        return FutureTradingResult.builder()
            .identifyCode(tradingResult.getIdentifyCode())
            .coinType(futureExchangeOrder.getCoinType())
            .tradingTerm(tradingResult.getTradingTerm())
            .orderType(futureExchangeOrder.getOrderType())
            .orderState(futureExchangeOrder.getOrderState())
            .fee(futureExchangeOrder.getPaidFee())
            .volume(futureExchangeOrder.getVolume())
            .price(futureExchangeOrder.getPrice())
            .priceType(futureExchangeOrder.getPriceType())
            .orderId(futureExchangeOrder.getOrderId())
            .tradingTag(tradingResult.getTradingTag())
            .createdAt(futureExchangeOrder.getCreatedAt())
            .build();
    }

    private FutureTradingResult makeTradingResult(TradingTask tradingTask, FutureExchangeOrder futureExchangeOrder) {
        return FutureTradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(futureExchangeOrder.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(futureExchangeOrder.getPaidFee())
            .priceType(tradingTask.getPriceType())
            .orderId(futureExchangeOrder.getOrderId())
            .tradingTag(tradingTask.getTag())
            .createdAt(futureExchangeOrder.getCreatedAt())
            .build();
    }

    private FutureTradingResult makeTradingResult(TradingTask tradingTask, FutureExchangeOrderCancel futureExchangeOrderCancel) { //fixme 중복 코드 개선
        return FutureTradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(futureExchangeOrderCancel.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(futureExchangeOrderCancel.getPaidFee())
            .priceType(tradingTask.getPriceType())
            .orderId(futureExchangeOrderCancel.getOrderId())
            .tradingTag(tradingTask.getTag())
            .createdAt(futureExchangeOrderCancel.getCreatedAt())
            .build();
    }
}
