package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.exchange.processor.UpbitExchangeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StrategyTest {

    @Autowired
    private UpbitExchangeProcessor upbitExchangeProcessor;

    @Test
    public void Prototype_테스트() { //fixme Mock을 이용해서 테스트 고도화(TDD)
        Strategy strategy = new PrototypeStrategy(upbitExchangeProcessor);
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
    }

    @Test
    public void Prototype2_테스트() {
        Strategy strategy = new Prototype2Strategy(upbitExchangeProcessor);
    }

}