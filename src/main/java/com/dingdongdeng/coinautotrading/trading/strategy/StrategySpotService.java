package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
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
public class StrategySpotService implements StrategyService<SpotTradingResult> {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final String keyPairId;
    private final SpotExchangeService spotExchangeService;

    @Override
    public TradingInfo getTradingInformation(String identifyCode) {
        SpotExchangeTradingInfoParam param = SpotExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();

        SpotExchangeTradingInfo spotExchangeTradingInfo = spotExchangeService.getTradingInformation(param, keyPairId);

        return TradingInfo.builder()
            .identifyCode(identifyCode)
            .coinExchangeType(spotExchangeTradingInfo.getCoinExchangeType())
            .coinType(spotExchangeTradingInfo.getCoinType())
            .tradingTerm(spotExchangeTradingInfo.getTradingTerm())
            .currency(spotExchangeTradingInfo.getCurrency())
            .candles(spotExchangeTradingInfo.getCandles())
            .balance(spotExchangeTradingInfo.getBalance())
            .locked(spotExchangeTradingInfo.getLocked())
            .avgBuyPrice(spotExchangeTradingInfo.getAvgBuyPrice())
            .unitCurrency(spotExchangeTradingInfo.getUnitCurrency())
            .currentPrice(spotExchangeTradingInfo.getTicker().getTradePrice())
            .rsi(spotExchangeTradingInfo.getRsi())
            .build();
    }

    @Override
    public SpotTradingResult order(TradingTask orderTradingTask) {
        SpotExchangeOrderParam param = SpotExchangeOrderParam.builder()
            .coinType(orderTradingTask.getCoinType())
            .orderType(orderTradingTask.getOrderType())
            .volume(orderTradingTask.getVolume())
            .price(orderTradingTask.getPrice())
            .priceType(orderTradingTask.getPriceType())
            .build();
        SpotExchangeOrder spotExchangeOrder = spotExchangeService.order(param, keyPairId);
        return makeTradingResult(orderTradingTask, spotExchangeOrder);
    }

    @Override
    public SpotTradingResult orderCancel(TradingTask cancelTradingTask) {
        SpotExchangeOrderCancelParam param = SpotExchangeOrderCancelParam.builder()
            .orderId(cancelTradingTask.getOrderId())
            .build();
        SpotExchangeOrderCancel spotExchangeOrderCancel = spotExchangeService.orderCancel(param, keyPairId);
        return makeTradingResult(cancelTradingTask, spotExchangeOrderCancel);
    }

    @Override
    public TradingResultPack<SpotTradingResult> updateTradingResultPack(TradingResultPack<SpotTradingResult> tradingResultPack) {
        return new TradingResultPack<SpotTradingResult>(
            updateTradingResultList(tradingResultPack.getBuyTradingResultList()),
            updateTradingResultList(tradingResultPack.getProfitTradingResultList()),
            updateTradingResultList(tradingResultPack.getLossTradingResultList())
        );
    }

    private List<SpotTradingResult> updateTradingResultList(List<SpotTradingResult> tradingResultList) {
        return tradingResultList.stream()
            .map(this::updateTradingResult)
            .collect(Collectors.toList());
    }

    private SpotTradingResult updateTradingResult(SpotTradingResult tradingResult) {
        SpotExchangeOrder spotExchangeOrder =
            spotExchangeService.getOrderInfo(SpotExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build(), keyPairId);
        return SpotTradingResult.builder()
            .identifyCode(tradingResult.getIdentifyCode())
            .coinType(spotExchangeOrder.getCoinType())
            .tradingTerm(tradingResult.getTradingTerm())
            .orderType(spotExchangeOrder.getOrderType())
            .orderState(spotExchangeOrder.getOrderState())
            .volume(spotExchangeOrder.getVolume())
            .price(spotExchangeOrder.getPrice())
            .priceType(spotExchangeOrder.getPriceType())
            .orderId(spotExchangeOrder.getOrderId())
            .tradingTag(tradingResult.getTradingTag())
            .createdAt(spotExchangeOrder.getCreatedAt())
            .build();
    }

    private SpotTradingResult makeTradingResult(TradingTask tradingTask, SpotExchangeOrder spotExchangeOrder) {
        return SpotTradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(spotExchangeOrder.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(spotExchangeOrder.getPaidFee() + spotExchangeOrder.getRemainingFee())
            .priceType(tradingTask.getPriceType())
            .orderId(spotExchangeOrder.getOrderId())
            .tradingTag(tradingTask.getTag())
            .createdAt(spotExchangeOrder.getCreatedAt())
            .build();
    }

    private SpotTradingResult makeTradingResult(TradingTask tradingTask, SpotExchangeOrderCancel spotExchangeOrderCancel) { //fixme 중복 코드 개선
        return SpotTradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(spotExchangeOrderCancel.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .fee(spotExchangeOrderCancel.getPaidFee() + spotExchangeOrderCancel.getRemainingFee())
            .priceType(tradingTask.getPriceType())
            .orderId(spotExchangeOrderCancel.getOrderId())
            .tradingTag(tradingTask.getTag())
            .createdAt(spotExchangeOrderCancel.getCreatedAt())
            .build();
    }

}
