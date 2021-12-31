package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Prototype2Strategy extends Strategy {

    //fixme stateful 해야함 , 임베디드 db든 필요함
    public Prototype2Strategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    public StrategyCode getCode() {
        return StrategyCode.PROTOTYPE2;
    }

    @Override
    protected OrderType what() {
        return OrderType.SELL;
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
