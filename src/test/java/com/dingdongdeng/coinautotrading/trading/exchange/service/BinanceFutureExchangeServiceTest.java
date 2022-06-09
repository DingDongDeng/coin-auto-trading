package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.*;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl.BinanceFutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitSpotExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@Slf4j
@SpringBootTest
class BinanceFutureExchangeServiceTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private BinanceFutureExchangeService binanceFutureExchangeService;

    @Value("${binance.future.client.accessKey}")
    private String accessKey;
    @Value("${binance.future.client.secretKey}")
    private String secretKey;

    private String userId = "123456";
    private String keyPairId;

    @BeforeEach
    public void setUp() {
        String keyPairId = UUID.randomUUID().toString();

        exchangeKeyRepository.save(
                ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("ACCESS_KEY")
                        .value(accessKey)
                        .userId(userId)
                        .build()
        );

        exchangeKeyRepository.save(
                ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("SECRET_KEY")
                        .value(secretKey)
                        .userId(userId)
                        .build()
        );

        this.keyPairId = keyPairId;

    }

    @Test
    public void 조회(){
        FutureExchangeTradingInfoParam param = FutureExchangeTradingInfoParam.builder()
            .coinType(CoinType.BITCOIN)
            .tradingTerm(TradingTerm.SCALPING)
            .build();

        FutureExchangeTradingInfo response = binanceFutureExchangeService.getTradingInformation(param, keyPairId);
        log.info("binanceTradingInfo result : {}", response);
    }

    @Test
    public void 주문(){
        FutureExchangeOrderParam param = FutureExchangeOrderParam.builder()
            .coinType(CoinType.BITCOIN)
            .orderType(OrderType.BUY)
            .priceType(PriceType.LIMIT_PRICE)
            .price(10000.0)
            .volume(0.009)
            .build();

        FutureExchangeOrder response = binanceFutureExchangeService.order(param, keyPairId);
        log.info("binanceTradingInfo result : {}", response);




    }

}