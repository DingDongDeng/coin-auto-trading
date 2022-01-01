package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrototypeStrategy extends Strategy {

    //fixme stateful 해야함 , 임베디드 db든 필요함
    public PrototypeStrategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    protected OrderType what() {
        return OrderType.BUY;
    }

    @Override
    protected boolean when(OrderType orderType) {
        return true;
    }

    @Override
    protected double how(OrderType orderType) {
        return 0;
    }
}
