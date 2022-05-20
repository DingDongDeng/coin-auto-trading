package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureRequest.*;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.BinanceFutureResponse.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class BinanceFutureClientTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private BinanceFutureClient binanceFutureClient;

    @Value("{accessKey}")
    private String accessKey;
    @Value("{secretKey}")
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
    public void 서버_시간_조회_테스트() {
        String time = binanceFutureClient.getServerTime();
        log.info("result : {}", time);
    }

    @Test
    public void 전체_계좌_조회_테스트() {
        Long nowTime = System.currentTimeMillis();
        FuturesAccountBalanceRequest request = FuturesAccountBalanceRequest.builder()
                .timestamp(nowTime)
                .build();
        List<FuturesAccountBalanceResponse> response = binanceFutureClient.getFuturesAccountBalance(request, keyPairId);
        for (FuturesAccountBalanceResponse balanceResponse: response) {
            if (balanceResponse.getBalance().equals("0.00000000")){
                continue;
            } else {
                System.out.println(balanceResponse.toString());
            }
        }
        log.info("result : {}", response);
    }

}