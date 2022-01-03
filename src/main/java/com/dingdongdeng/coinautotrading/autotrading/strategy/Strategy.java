package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.What;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Strategy {

    protected final ExchangeProcessor processor;

    public void execute() {
        What what = what();
        if (when(what)) {
            if (isOrder(what)) {
                processor.order(makeProcessOrderParam());
            }
            if (isOrderCancel(what)) {
                processor.orderCancel(makeProcessOrderCancelParam());
            }
        }
    }

    abstract protected What what();

    abstract protected boolean when(What what);

    private boolean isOrder(What what) {
        OrderType orderType = what.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(What what) {
        OrderType orderType = what.getOrderType();
        return orderType == OrderType.CANCEL;
    }

    private ProcessOrderParam makeProcessOrderParam() {
        return ProcessOrderParam.builder().build();
    }

    private ProcessOrderCancelParam makeProcessOrderCancelParam() {
        return ProcessOrderCancelParam.builder().build();
    }

}
