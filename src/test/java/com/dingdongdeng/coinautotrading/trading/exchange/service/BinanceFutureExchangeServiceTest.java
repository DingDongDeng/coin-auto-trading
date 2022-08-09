package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Symbol;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl.BinanceFutureExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderCancelParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeOrderParam;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.model.FutureExchangeTradingInfoParam;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

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
            .tradingTerm(TradingTerm.SCALPING_15M)
            .build();

        FutureExchangeTradingInfo response = binanceFutureExchangeService.getTradingInformation(param, keyPairId);
        log.info("binanceTradingInfo result : {}", response);
    }

    @Test
    public void 주문(){
        FutureExchangeOrderParam param = FutureExchangeOrderParam.builder()
            .coinType(CoinType.BITCOIN)
            .orderType(OrderType.BUY)
            .priceType(PriceType.LIMIT)
            .price(10000.0)
            .volume(0.009)
            .build();
        FutureExchangeOrder response = binanceFutureExchangeService.order(param, keyPairId);
        log.info("binanceOrderInfo result : {}", response);


        FutureExchangeOrderInfoParam infoParam = FutureExchangeOrderInfoParam.builder()
            .orderId(response.getOrderId())
            .symbol(Symbol.of(response.getCoinType()).getCode())
            .build();
        FutureExchangeOrder infoResponse = binanceFutureExchangeService.getOrderInfo(infoParam, keyPairId);
        log.info("binanceTradingInfo result : {}", infoResponse);


        FutureExchangeOrderCancelParam cancelParam = FutureExchangeOrderCancelParam.builder()
            .orderId(response.getOrderId())
            .symbol(Symbol.of(response.getCoinType()).getCode())
            .build();
        FutureExchangeOrderCancel cancelResponse = binanceFutureExchangeService.orderCancel(cancelParam, keyPairId);
        log.info("binanceCancel result : {}", cancelResponse);

    }

}