package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Prototype2Strategy extends Strategy {

    private final ExchangeProcessor processor;

    @Override
    public void execute() {
        log.info("sample prototype2Strategy");
    }

    @Override
    public StrategyCode getCode() {
        return StrategyCode.PROTOTYPE2;
    }

    @Override
    protected OrderType what() {
        return null;
    }

    @Override
    protected boolean when(OrderType orderType) {
        return false;
    }

    @Override
    protected double how(OrderType orderType) {
        return 0;
    }
}
