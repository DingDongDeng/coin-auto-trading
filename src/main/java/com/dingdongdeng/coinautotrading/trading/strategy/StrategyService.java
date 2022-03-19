package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class StrategyService {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final String keyPairId;
    private final ExchangeService exchangeService;

    public TradingInfo getTradingInformation(String identifyCode, TradingResultPack tradingResultPack) {
        ExchangeTradingInfoParam param = ExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();
        ExchangeTradingInfo exchangeTradingInfo = exchangeService.getTradingInformation(param, keyPairId);

        return TradingInfo.builder()
            .identifyCode(identifyCode)
            .coinExchangeType(exchangeTradingInfo.getCoinExchangeType())
            .coinType(exchangeTradingInfo.getCoinType())
            .tradingTerm(exchangeTradingInfo.getTradingTerm())
            .currency(exchangeTradingInfo.getCurrency())
            .balance(exchangeTradingInfo.getBalance())
            .locked(exchangeTradingInfo.getLocked())
            .avgBuyPrice(exchangeTradingInfo.getAvgBuyPrice())
            .unitCurrency(exchangeTradingInfo.getUnitCurrency())
            .currentPrice(exchangeTradingInfo.getTicker().getTradePrice())
            .tradingResultPack(tradingResultPack)
            .rsi(exchangeTradingInfo.getRsi())
            .build();
    }

    public TradingResult order(TradingTask orderTradingTask) {
        ExchangeOrderParam param = ExchangeOrderParam.builder()
            .coinType(orderTradingTask.getCoinType())
            .orderType(orderTradingTask.getOrderType())
            .volume(orderTradingTask.getVolume())
            .price(orderTradingTask.getPrice())
            .priceType(orderTradingTask.getPriceType())
            .build();
        ExchangeOrder exchangeOrder = exchangeService.order(param, keyPairId);
        return makeTradingResult(orderTradingTask, exchangeOrder);
    }

    public TradingResult orderCancel(TradingTask cancelTradingTask) {
        ExchangeOrderCancelParam param = ExchangeOrderCancelParam.builder()
            .orderId(cancelTradingTask.getOrderId())
            .build();
        ExchangeOrderCancel exchangeOrderCancel = exchangeService.orderCancel(param, keyPairId);
        return makeTradingResult(cancelTradingTask, exchangeOrderCancel);
    }

    public TradingResultPack updateTradingResultPack(TradingResultPack tradingResultPack) {
        return TradingResultPack.builder()
            .buyTradingResult(updateTradingResult(tradingResultPack.getBuyTradingResult()))
            .profitTradingResult(updateTradingResult(tradingResultPack.getProfitTradingResult()))
            .lossTradingResult(updateTradingResult(tradingResultPack.getLossTradingResult()))
            .build();
    }

    private TradingResult updateTradingResult(TradingResult tradingResult) {
        if (!tradingResult.isExist()) {
            return new TradingResult();
        }
        ExchangeOrder exchangeOrder =
            exchangeService.getOrderInfo(ExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build(), keyPairId);
        return TradingResult.builder()
            .id(tradingResult.getId())
            .identifyCode(tradingResult.getIdentifyCode())
            .coinType(tradingResult.getCoinType())
            .orderType(tradingResult.getOrderType())
            .orderState(exchangeOrder.getOrderState())
            .volume(tradingResult.getVolume())
            .price(tradingResult.getPrice())
            .priceType(tradingResult.getPriceType())
            .orderId(exchangeOrder.getOrderId())
            .tag(tradingResult.getTag())
            .createdAt(exchangeOrder.getCreatedAt())
            .build();
    }

    private TradingResult makeTradingResult(TradingTask tradingTask, ExchangeOrder exchangeOrder) {
        return TradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(exchangeOrder.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .priceType(tradingTask.getPriceType())
            .orderId(exchangeOrder.getOrderId())
            .tag(tradingTask.getTag())
            .createdAt(exchangeOrder.getCreatedAt())
            .build();
    }

    private TradingResult makeTradingResult(TradingTask tradingTask, ExchangeOrderCancel exchangeOrderCancel) { //fixme 중복 코드 개선
        return TradingResult.builder()
            .identifyCode(tradingTask.getIdentifyCode())
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .tradingTerm(tradingTask.getTradingTerm())
            .orderState(exchangeOrderCancel.getOrderState())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .priceType(tradingTask.getPriceType())
            .orderId(exchangeOrderCancel.getOrderId())
            .tag(tradingTask.getTag())
            .createdAt(exchangeOrderCancel.getCreatedAt())
            .build();
    }

}
