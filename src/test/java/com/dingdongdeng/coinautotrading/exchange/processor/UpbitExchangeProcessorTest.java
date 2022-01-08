package com.dingdongdeng.coinautotrading.exchange.processor;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedTradingInfo;
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
        ProcessOrderParam orderParam = ProcessOrderParam.builder()
            .coinType(CoinType.ETHEREUM)
            .orderType(OrderType.BUY)
            .volume(1.0)
            .price(5000.0)
            .priceType(PriceType.LIMIT_PRICE)
            .build();
        ProcessedOrder orderResult = processor.order(orderParam);
        log.info("order result : {}", orderResult);

        ProcessOrderCancelParam cancelParam = ProcessOrderCancelParam.builder()
            .orderId(orderResult.getOrderId())
            .build();
        ProcessedOrderCancel cancelResult = processor.orderCancel(cancelParam);
        log.info("cancel result : {}", cancelResult);
    }

    @Test
    public void 거래를_위한_정보_생성_테스트() {
        ProcessedTradingInfo processedTradingInfo = processor.getTradingInformation(
            ProcessTradingInfoParam.builder()
                .coinType(CoinType.ETHEREUM)
                .tradingTerm(TradingTerm.SCALPING)
                .build()
        );
        log.info("tradingInfo result : {}", processedTradingInfo);
    }

}