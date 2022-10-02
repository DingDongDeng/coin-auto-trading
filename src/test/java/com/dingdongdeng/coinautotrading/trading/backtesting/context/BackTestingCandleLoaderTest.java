package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitSpotExchangeCandleService;
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
class BackTestingCandleLoaderTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitSpotExchangeCandleService exchangeCandleService;

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
    public void 백테스팅_분봉_캔들_로딩_테스트() {

        //given
        LocalDateTime start = LocalDateTime.of(2022, 03, 25, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 03, 25, 14, 6, 14);
        BackTestingCandleLoader backTestingCandleLoader = BackTestingCandleLoader.builder()
            .coinType(CoinType.ETHEREUM)
            .keyPairdId(keyPairId)
            .exchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
            .candleUnit(CandleUnit.UNIT_1M)
            .build();

        for (int i = 0; i < 199; i++) {
            Candle candle = backTestingCandleLoader.getNextCandle();
        }

        Candle lastCandle = backTestingCandleLoader.getNextCandle();
        assertEquals(start.plusMinutes(200).withSecond(0), lastCandle.getCandleDateTimeKst());

        for (int i = 0; i < 4; i++) {
            backTestingCandleLoader.getNextCandle();
        }
        Candle lastCandle2 = backTestingCandleLoader.getNextCandle();
        assertEquals(end.withSecond(0), lastCandle2.getCandleDateTimeKst());

        assertEquals(null, backTestingCandleLoader.getNextCandle());

    }

    @Test
    public void 백테스팅_일봉_캔들_로딩_테스트() {

        //given
        LocalDateTime start = LocalDateTime.of(2022, 1, 6, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 7, 30, 9, 6, 14);
        BackTestingCandleLoader backTestingCandleLoader = BackTestingCandleLoader.builder()
            .coinType(CoinType.ETHEREUM)
            .keyPairdId(keyPairId)
            .exchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
            .candleUnit(CandleUnit.UNIT_1D)
            .build();

        for (int i = 0; i < 199; i++) {
            Candle candle = backTestingCandleLoader.getNextCandle();
        }

        Candle lastCandle = backTestingCandleLoader.getNextCandle();
        assertEquals(start.plusDays(200).withHour(9).withMinute(0).withMinute(0).withSecond(0), lastCandle.getCandleDateTimeKst());

        for (int i = 0; i < 4; i++) {
            backTestingCandleLoader.getNextCandle();
        }
        Candle lastCandle2 = backTestingCandleLoader.getNextCandle();
        assertEquals(end.withHour(9).withMinute(0).withMinute(0).withSecond(0), lastCandle2.getCandleDateTimeKst());

        assertEquals(null, backTestingCandleLoader.getNextCandle());

    }
}