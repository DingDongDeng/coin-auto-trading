package com.dingdongdeng.coinautotrading.trading.domain.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.trading.domain.entity.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ExchangeOrderComponentTest {

    @Autowired
    private ExchangeOrderComponent exchangeOrderComponent;

    @Test
    public void 저장과_조회_테스트() {
        ExchangeOrder savedExchangeOrder = exchangeOrderComponent.save(ExchangeOrder.builder()
            .orderType(OrderType.BUY)
            .build());
        ExchangeOrder exchangeOrder = exchangeOrderComponent.findById(savedExchangeOrder.getId());
        assertEquals(exchangeOrder.getId(), savedExchangeOrder.getId());
    }
}