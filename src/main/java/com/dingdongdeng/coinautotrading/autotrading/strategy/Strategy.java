package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    private final ExchangeProcessor processor;
    private final Stack<ProcessOrderResult> unDecidedOrderStack = new Stack<>();

    public void execute() {
        //fixme 미체결된 주문 확인 로직 필요
        OrderTask orderTask = makeOrderTask(processor.getAccount(ProcessAccountParam.builder().build()), unDecidedOrderStack);
        if (isOrder(orderTask)) {
            ProcessOrderResult orderResult = processor.order(makeProcessOrderParam(orderTask));
            if (orderResult.getOrderState() == OrderState.WAIT) {
                unDecidedOrderStack.push(orderResult);
            }
            return;
        }
        if (isOrderCancel(orderTask)) {
            processor.orderCancel(makeProcessOrderCancelParam(orderTask));
        }
    }

    abstract protected OrderTask makeOrderTask(ProcessAccountResult account, Stack<ProcessOrderResult> unDecidedOrderStack);

    private boolean isOrder(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.CANCEL;
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
