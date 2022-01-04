package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import java.util.Stack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Prototype2Strategy extends Strategy {

    public Prototype2Strategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    protected OrderTask makeOrderTask(ProcessAccountResult account, Stack<ProcessOrderResult> unDecidedOrderStack) {
        return OrderTask.builder().orderType(OrderType.SELL).build();
    }
}
