package com.dingdongdeng.coinautotrading.exchange.processor;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitExchangeProcessorTest {

    @Autowired
    private UpbitExchangeProcessor processor;

    @Test
    public void 계좌_조회_테스트() {
        ProcessAccountParam param = ProcessAccountParam.builder().build();
        log.info("result : {}", processor.getAccount(param));
    }

    @Test
    public void 주문과_조회와_취소_프로세스_테스트() {
        ProcessOrderParam orderParam = ProcessOrderParam.builder()
            .marketId("KRW-ETH")
            .orderType(OrderType.BUY)
            .volume(1.0)
            .price(5000.0)
            .priceType(PriceType.LIMIT_PRICE)
            .build();
        ProcessOrderResult orderResult = processor.order(orderParam);
        log.info("order result : {}", orderResult);

        ProcessOrderInfoParam orderInfoParam = ProcessOrderInfoParam.builder()
            .orderId(orderResult.getOrderId())
            .build();
        ProcessOrderInfoResult orderInfoResult = processor.getOrderInfo(orderInfoParam);
        log.info("orderInfo result : {}", orderInfoResult);

        ProcessOrderCancelParam cancelParam = ProcessOrderCancelParam.builder()
            .orderId(orderInfoResult.getOrderId())
            .build();
        ProcessOrderCancelResult cancelResult = processor.orderCancel(cancelParam);
        log.info("cancel result : {}", cancelResult);
    }
}