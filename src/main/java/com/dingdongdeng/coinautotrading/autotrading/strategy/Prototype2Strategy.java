package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.What;
import com.dingdongdeng.coinautotrading.autotrading.type.TaskType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Prototype2Strategy extends Strategy {

    //fixme stateful 해야함 , 임베디드 db든 필요함
    public Prototype2Strategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    protected What what() {
        return What.builder().taskType(TaskType.SELL).build();
    }

    @Override
    protected boolean when(What what) {
        return true;
    }
}
