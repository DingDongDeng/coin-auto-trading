package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class StrategyFactory {

    public Strategy create(StrategyCode code, ExchangeProcessor processor) {
        if (code == StrategyCode.PROTOTYPE) {
            return new PrototypeStrategy(processor);
        }
        if (code == StrategyCode.PROTOTYPE2) {
            return new Prototype2Strategy(processor);
        }

        throw new NoSuchElementException("not found strategy code : " + code);
    }
}
