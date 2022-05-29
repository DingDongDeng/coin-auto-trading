package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StrategyTest {

    @Autowired
    private UpbitExchangeService upbitExchangeProcessor;

    @Test
    public void Prototype_테스트() { //fixme Mock을 이용해서 테스트 고도화(TDD)
//        Strategy strategy = new RsiTradingStrategy(CoinType.ETHEREUM, TradingTerm.SCALPING, upbitExchangeProcessor);
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
    }

}