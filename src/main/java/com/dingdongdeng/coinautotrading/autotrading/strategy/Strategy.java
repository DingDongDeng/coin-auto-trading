package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.domain.entity.ExchangeOrder;
import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final ExchangeProcessor processor;
    private final List<ExchangeOrder> unDecidedExchangeOrderList = new ArrayList<>();

    public OrderTask execute() {
        log.info("unDecidedExchangeOrderList : {}", unDecidedExchangeOrderList);
        updateUndecideOrders(unDecidedExchangeOrderList);

        OrderTask orderTask = makeOrderTask(processor.getAccount(ProcessAccountParam.builder().build()));
        log.info("orderTask : {}", orderTask);

        if (isOrder(orderTask)) {
            ExchangeOrder exchangeOrder = makeExchangeOrder(processor.order(makeProcessOrderParam(orderTask)));
            if (exchangeOrder.getOrderState() == OrderState.WAIT) {
                unDecidedExchangeOrderList.add(exchangeOrder);
            }
            return orderTask;
        }
        if (isOrderCancel(orderTask)) {
            processor.orderCancel(makeProcessOrderCancelParam(orderTask));
            return orderTask;
        }

        return orderTask;
    }

    abstract protected OrderTask makeOrderTask(ProcessAccountResult account);

    private void updateUndecideOrders(List<ExchangeOrder> unDecidedExchangeOrderList) {
        List<ExchangeOrder> updatedExchangeOrderList = unDecidedExchangeOrderList.stream()
            .map(exchangeOrder -> {
                ProcessOrderInfoParam orderInfoParam = ProcessOrderInfoParam.builder().orderId(exchangeOrder.getOrderId()).build();
                return makeExchangeOrder(processor.getOrderInfo(orderInfoParam));
            })
            .filter(exchangeOrder -> exchangeOrder.getOrderState() == OrderState.WAIT)
            .collect(Collectors.toList());

        unDecidedExchangeOrderList.clear();
        unDecidedExchangeOrderList.addAll(updatedExchangeOrderList);
        log.info("updatedUndecideOrderList :  {}", updatedExchangeOrderList);
    }

    private boolean isOrder(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ExchangeOrder makeExchangeOrder(ProcessOrderResult processOrderResult) {
        return ExchangeOrder.builder()
            .id(null)
            .orderId(processOrderResult.getOrderId())
            .orderType(processOrderResult.getOrderType())
            .priceType(processOrderResult.getPriceType())
            .price(processOrderResult.getPrice())
            .avgPrice(processOrderResult.getAvgPrice())
            .orderState(processOrderResult.getOrderState())
            .market(processOrderResult.getMarket())
            .createdAt(processOrderResult.getCreatedAt())
            .volume(processOrderResult.getVolume())
            .remainingVolume(processOrderResult.getRemainingVolume())
            .reservedFee(processOrderResult.getReservedFee())
            .remainingFee(processOrderResult.getRemainingFee())
            .paidFee(processOrderResult.getPaidFee())
            .locked(processOrderResult.getLocked())
            .executedVolume(processOrderResult.getExecutedVolume())
            .tradeCount(processOrderResult.getTradeCount())
            .build();
    }

    private ExchangeOrder makeExchangeOrder(ProcessOrderInfoResult processOrderInfoResult) {
        return ExchangeOrder.builder()
            .id(null)
            .orderId(processOrderInfoResult.getOrderId())
            .orderType(processOrderInfoResult.getOrderType())
            .priceType(processOrderInfoResult.getPriceType())
            .price(processOrderInfoResult.getPrice())
            .avgPrice(processOrderInfoResult.getAvgPrice())
            .orderState(processOrderInfoResult.getOrderState())
            .market(processOrderInfoResult.getMarket())
            .createdAt(processOrderInfoResult.getCreatedAt())
            .volume(processOrderInfoResult.getVolume())
            .remainingVolume(processOrderInfoResult.getRemainingVolume())
            .reservedFee(processOrderInfoResult.getReservedFee())
            .remainingFee(processOrderInfoResult.getRemainingFee())
            .paidFee(processOrderInfoResult.getPaidFee())
            .locked(processOrderInfoResult.getLocked())
            .executedVolume(processOrderInfoResult.getExecutedVolume())
            .tradeCount(processOrderInfoResult.getTradeCount())
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
