package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfoParam;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitExchangeServiceTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitExchangeService upbitExchangeService;

    @Value("${upbit.client.accessKey}")
    private String accessKey;
    @Value("${upbit.client.secretKey}")
    private String secretKey;

    private String userId = "123456";
    private String keyPairId;

    @BeforeEach
    public void setUp() {
        String keyPairId = UUID.randomUUID().toString();
        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("ACCESS_KEY")
                .value(accessKey)
                .userId(userId)
                .build()
        );

        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("SECRET_KEY")
                .value(secretKey)
                .userId(userId)
                .build()
        );

        this.keyPairId = keyPairId;

    }

    @Test
    public void 주문과_조회와_취소_프로세스_테스트() {
        ExchangeOrderParam exchangeOrderParam = ExchangeOrderParam.builder()
            .coinType(CoinType.ETHEREUM)
            .orderType(OrderType.BUY)
            .volume(1.0)
            .price(5000.0)
            .priceType(PriceType.LIMIT_PRICE)
            .build();
        ExchangeOrder exchangeOrderResult = upbitExchangeService.order(exchangeOrderParam, keyPairId);
        log.info("exchangeOrder result : {}", exchangeOrderResult);

        ExchangeOrder exchangeOrderInfoResult = upbitExchangeService.getOrderInfo(ExchangeOrderInfoParam.builder().orderId(exchangeOrderResult.getOrderId()).build(), keyPairId);
        log.info("exchangeOrderInfo result : {}", exchangeOrderInfoResult);

        ExchangeOrderCancelParam cancelParam = ExchangeOrderCancelParam.builder()
            .orderId(exchangeOrderResult.getOrderId())
            .build();
        ExchangeOrderCancel cancelResult = upbitExchangeService.orderCancel(cancelParam, keyPairId);
        log.info("exchangeCancel result : {}", cancelResult);
    }

    @Test
    public void 거래를_위한_정보_생성_테스트() {
        ExchangeTradingInfo exchangeTradingInfo = upbitExchangeService.getTradingInformation(
            ExchangeTradingInfoParam.builder()
                .coinType(CoinType.ETHEREUM)
                .tradingTerm(TradingTerm.SCALPING)
                .now(LocalDateTime.now())
                .build(),
            keyPairId
        );
        log.info("exchangeTradingInfo result : {}", exchangeTradingInfo);
    }

}