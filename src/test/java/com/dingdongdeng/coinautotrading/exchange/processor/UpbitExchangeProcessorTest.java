package com.dingdongdeng.coinautotrading.exchange.processor;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
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
    public void 주문과_조회와_취소_프로세스_테스트() {
        ProcessOrderParam param = ProcessOrderParam.builder()
            .marketId("KRW-ETH")
            .orderType(OrderType.BUYING)
            .volume(1.0)
            .price(5000.0)
            .priceType(PriceType.LIMIT_PRICE)
            .build();
        log.info("result : {}", processor.order(param));

    }
}