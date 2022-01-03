package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.What;
import com.dingdongdeng.coinautotrading.autotrading.type.TaskType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrototypeStrategy extends Strategy {

    //fixme 백테스팅 고려하기
    //fixme stateful 해야함 , 임베디드 db든 필요함
    public PrototypeStrategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    protected What what() {
        return What.builder().taskType(TaskType.BUY).build();
    }

    @Override
    protected boolean when(What what) {
        return true;
    }
}
