package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final ExchangeService exchangeService;

    public void execute() {

        ExchangeTradingInfo exchangeTradingInfo = exchangeService.getTradingInformation(makeExchangeTradingInfoParam(coinType, tradingTerm));

        List<TradingTask> tradingTaskList = this.makeTradingTask(exchangeTradingInfo);
        log.info("tradingTaskList : {}", tradingTaskList);

        tradingTaskList.forEach(tradingTask -> {
            // 매수, 매도 주문
            if (isOrder(tradingTask)) {
                exchangeService.order(makeExchangeOrderParam(tradingTask));
                return;
            }

            // 주문 취소
            if (isOrderCancel(tradingTask)) {
                exchangeService.orderCancel(makeExchangeOrderCancelParam(tradingTask));
            }

            // 아무것도 하지 않음
        });
    }

    abstract protected List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo);

    private boolean isOrder(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ExchangeTradingInfoParam makeExchangeTradingInfoParam(CoinType coinType, TradingTerm tradingTerm) {
        return ExchangeTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();
    }

    private ExchangeOrderParam makeExchangeOrderParam(TradingTask tradingTask) {
        return ExchangeOrderParam.builder()
            .coinType(tradingTask.getCoinType())
            .orderType(tradingTask.getOrderType())
            .volume(tradingTask.getVolume())
            .price(tradingTask.getPrice())
            .priceType(tradingTask.getPriceType())
            .build();
    }

    private ExchangeOrderCancelParam makeExchangeOrderCancelParam(TradingTask tradingTask) {
        return ExchangeOrderCancelParam.builder()
            .orderId(tradingTask.getOrderId())
            .build();
    }

}
