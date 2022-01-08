package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.autotrading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedTradingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final ExchangeProcessor processor;

    public void execute() {

        TradingInfo tradingInfo = makeTradingInfo(coinType, tradingTerm);
        log.info("tradingInfo : {}", tradingInfo);

        OrderTask orderTask = this.makeOrderTask(tradingInfo);
        log.info("orderTask : {}", orderTask);

        // 매수, 매도 주문
        if (isOrder(orderTask)) {
            processor.order(makeProcessOrderParam(orderTask));
            return;
        }

        // 주문 취소
        if (isOrderCancel(orderTask)) {
            processor.orderCancel(makeProcessOrderCancelParam(orderTask));
        }

        // 아무것도 하지 않음
    }

    abstract protected OrderTask makeOrderTask(TradingInfo tradingInfo);

    private boolean isOrder(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private TradingInfo makeTradingInfo(CoinType coinType, TradingTerm tradingTerm) { //fixme processor가 여기있는게 맘에 안듦
        ProcessTradingInfoParam tradingInfoParam = ProcessTradingInfoParam.builder()
            .coinType(coinType)
            .tradingTerm(tradingTerm)
            .build();

        ProcessedTradingInfo tradingInfo = processor.getTradingInformation(tradingInfoParam);

        return TradingInfo.builder()
            .rsi(tradingInfo.getRsi())
            .build();
    }

    private ProcessOrderParam makeProcessOrderParam(OrderTask orderTask) {
        return ProcessOrderParam.builder()
            .coinType(orderTask.getCoinType())
            .orderType(orderTask.getOrderType())
            .volume(orderTask.getVolume())
            .price(orderTask.getPrice())
            .priceType(orderTask.getPriceType())
            .build();
    }

    private ProcessOrderCancelParam makeProcessOrderCancelParam(OrderTask orderTask) {
        return ProcessOrderCancelParam.builder()
            .orderId(orderTask.getOrderId())
            .build();
    }

}
