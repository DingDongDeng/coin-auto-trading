package com.dingdongdeng.coinautotrading.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.domain.entity.TradeOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TradeTradeOrderServiceTest {

  @Autowired private TradeOrderService tradeOrderService;

  @Test
  public void 저장과_조회_테스트() {
    TradeOrder savedTradeOrder =
        tradeOrderService.save(TradeOrder.builder().orderType(OrderType.BUY).build());
    TradeOrder tradeOrder = tradeOrderService.findById(savedTradeOrder.getId());
    assertEquals(tradeOrder.getId(), savedTradeOrder.getId());
  }
}
