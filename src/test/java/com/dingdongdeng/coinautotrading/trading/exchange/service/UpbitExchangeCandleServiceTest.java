package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
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
class UpbitExchangeCandleServiceTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitExchangeCandleService service;

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
    public void 캔들_1분봉_조회_테스트() {
        LocalDateTime start = LocalDateTime.of(2022, 01, 13, 21, 9, 0);
        LocalDateTime end = LocalDateTime.of(2022, 01, 13, 21, 19, 0);
        log.info("start : {}", start);
        log.info("end : {}", end);
        ExchangeCandles candles = service.getCandleList(CoinType.ETHEREUM, CandleUnit.UNIT_1M, start, end, keyPairId);
        log.info("size : {}, candles: {}", candles.getCandleList().size(), candles);

        assertEquals(9, candles.getCandleList().size());
    }

}