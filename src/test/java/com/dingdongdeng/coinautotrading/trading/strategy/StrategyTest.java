package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.component.IndexCalculator;
import com.dingdongdeng.coinautotrading.exchange.service.UpbitExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StrategyTest {

    @Autowired
    private UpbitExchangeService upbitExchangeProcessor;
    @Autowired
    private IndexCalculator indexCalculator;

    @Test
    public void Prototype_테스트() { //fixme Mock을 이용해서 테스트 고도화(TDD)
        Strategy strategy = new RsiTradingStrategy(CoinType.ETHEREUM, TradingTerm.SCALPING, upbitExchangeProcessor, indexCalculator);
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
        //log.info("orderTask : {}", strategy.execute());
    }

}