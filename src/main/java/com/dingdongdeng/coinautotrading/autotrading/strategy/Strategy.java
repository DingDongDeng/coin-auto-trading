package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    protected final ExchangeProcessor processor;

    public void execute() {
        log.info("prototype strategy");

        OrderType orderType = what();
        if (when(orderType)) {
            if (orderType == OrderType.BUY || orderType == OrderType.SELL) {
                processor.order(orderType);
            }
            if (orderType == OrderType.ORDER_CANCEL) {
                processor.orderCancel();
            }
        }

    }

    abstract protected OrderType what();

    abstract protected boolean when(OrderType orderType);

    abstract protected double how(OrderType orderType);

}
