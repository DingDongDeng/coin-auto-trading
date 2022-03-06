package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final CoinType coinType;
    private final TradingTerm tradingTerm;
    private final String keyPairId;
    private final ExchangeService exchangeService;

    public void execute() {

        ExchangeTradingInfo exchangeTradingInfo = exchangeService.getTradingInformation(makeExchangeTradingInfoParam(), keyPairId);

        List<TradingTask> tradingTaskList = this.makeTradingTask(exchangeTradingInfo);
        log.info("tradingTaskList : {}", tradingTaskList);

        tradingTaskList.forEach(tradingTask -> {
            // 매수, 매도 주문
            if (isOrder(tradingTask)) {
                ExchangeOrder order = exchangeService.order(makeExchangeOrderParam(tradingTask), keyPairId);
                this.handleOrderResult(order, makeTradingResult(tradingTask, order));
                return;
            }

            // 주문 취소
            if (isOrderCancel(tradingTask)) {
                ExchangeOrderCancel orderCancel = exchangeService.orderCancel(makeExchangeOrderCancelParam(tradingTask), keyPairId);
                this.handleOrderCancelResult(orderCancel, makeTradingResult(tradingTask, orderCancel));
            }

            // 아무것도 하지 않음
        });
    }

    abstract public String getIdentifyCode();

    abstract protected List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo);

    abstract protected void handleOrderResult(ExchangeOrder order, TradingResult tradingResult);

    abstract protected void handleOrderCancelResult(ExchangeOrderCancel orderCancel, TradingResult tradingResult);

    private boolean isOrder(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ExchangeTradingInfoParam makeExchangeTradingInfoParam() {
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
