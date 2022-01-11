package com.dingdongdeng.coinautotrading.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.domain.entity.Trading;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TradingServiceTest {

    @Autowired
    private TradingService tradingService;

    @Test
    public void 저장과_조회_테스트() {
        Trading savedTrading = tradingService.save(Trading.builder()
            .orderType(OrderType.BUY)
            .build());
        Trading trading = tradingService.findById(savedTrading.getId());
        assertEquals(trading.getId(), savedTrading.getId());
    }
}