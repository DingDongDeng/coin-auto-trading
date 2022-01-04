package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    protected final ExchangeProcessor processor;
    protected final Stack<OrderTask> orderTaskStack = new Stack<>();

    public void execute() {
        OrderTask orderTask = makeOrderTask();
        if (isOrder(orderTask)) {
            processor.order(makeProcessOrderParam());
            return;
        }
        if (isOrderCancel(orderTask)) {
            processor.orderCancel(makeProcessOrderCancelParam());
        }
    }

    abstract protected OrderTask makeOrderTask();

    private boolean isOrder(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(OrderTask orderTask) {
        OrderType orderType = orderTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ProcessOrderParam makeProcessOrderParam() {
        return ProcessOrderParam.builder().build();
    }

    private ProcessOrderCancelParam makeProcessOrderCancelParam() {
        return ProcessOrderCancelParam.builder().build();
    }

}
