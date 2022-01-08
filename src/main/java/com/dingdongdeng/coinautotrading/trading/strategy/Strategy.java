package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.index.service.IndexCalculator;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final ExchangeService exchangeService;
    private final IndexCalculator indexCalculator;

    public void execute() {

        TradingInfo tradingInfo = makeTradingInfo(coinType, tradingTerm);
        log.info("tradingInfo : {}", tradingInfo);

        TradingTask tradingTask = this.makeTradingTask(tradingInfo);
        log.info("tradingTask : {}", tradingTask);

        // 매수, 매도 주문
        if (isOrder(tradingTask)) {
            exchangeService.order(makeProcessOrderParam(tradingTask));
            return;
        }

        // 주문 취소
        if (isOrderCancel(tradingTask)) {
            exchangeService.orderCancel(makeProcessOrderCancelParam(tradingTask));
        }

        // 아무것도 하지 않음
    }

    abstract protected TradingTask makeTradingTask(TradingInfo tradingInfo);

    private TradingInfo makeTradingInfo(CoinType coinType, TradingTerm tradingTerm) { //fixme processor가 여기있는게 맘에 안듦
        ExchangeTradingInfoParam exchangeTradingInfoParam = ExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();

        ExchangeTradingInfo exchangeTradingInfo = exchangeService.getTradingInformation(exchangeTradingInfoParam);

        return TradingInfo.builder()
            .coinType(exchangeTradingInfo.getCoinType())
            .rsi(indexCalculator.getRsi(exchangeTradingInfo.getCandles()))
            .build();
    }

    private boolean isOrder(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ExchangeOrderParam makeProcessOrderParam(TradingTask tradingTask) {
        return ExchangeOrderParam.builder()
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .priceType(tradingTask.getPriceType())
            .build();
    }

    private ExchangeOrderCancelParam makeProcessOrderCancelParam(TradingTask tradingTask) {
        return ExchangeOrderCancelParam.builder()
            .orderId(tradingTask.getOrderId())
            .build();
    }

}
