package com.dingdongdeng.coinautotrading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfoParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitExchangeServiceTest {

    @Autowired
    private UpbitExchangeService upbitExchangeService;

    @Test
    public void 주문과_조회와_취소_프로세스_테스트() {
        ExchangeOrderParam exchangeOrderParam = ExchangeOrderParam.builder()
            .coinType(CoinType.ETHEREUM)
            .orderType(OrderType.BUY)
            .volume(1.0)
            .price(5000.0)
            .priceType(PriceType.LIMIT_PRICE)
            .build();
        ExchangeOrder exchangeOrderResult = upbitExchangeService.order(exchangeOrderParam);
        log.info("exchangeOrder result : {}", exchangeOrderResult);

        ExchangeOrder exchangeOrderInfoResult = upbitExchangeService.getOrderInfo(ExchangeOrderInfoParam.builder().orderId(exchangeOrderResult.getOrderId()).build());
        log.info("exchangeOrderInfo result : {}", exchangeOrderInfoResult);

        ExchangeOrderCancelParam cancelParam = ExchangeOrderCancelParam.builder()
            .orderId(exchangeOrderResult.getOrderId())
            .build();
        ExchangeOrderCancel cancelResult = upbitExchangeService.orderCancel(cancelParam);
        log.info("exchangeCancel result : {}", cancelResult);
    }

    @Test
    public void 거래를_위한_정보_생성_테스트() {
        ExchangeTradingInfo exchangeTradingInfo = upbitExchangeService.getTradingInformation(
            ExchangeTradingInfoParam.builder()
                .coinType(CoinType.ETHEREUM)
                .tradingTerm(TradingTerm.SCALPING)
                .build()
        );
        log.info("exchangeTradingInfo result : {}", exchangeTradingInfo);
    }

}