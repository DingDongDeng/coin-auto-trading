package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.SpotExchangeCandles.Candle;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitSpotExchangeCandleService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@Slf4j
@SpringBootTest
class BackTestingCandleLoaderTest {

    @MockBean
    private ExchangeKeyService exchangeKeyService;
    @Autowired
    private UpbitSpotExchangeCandleService exchangeCandleService;

    @Value("${upbit.client.accessKey}")
    private String accessKey;
    @Value("${upbit.client.secretKey}")
    private String secretKey;

    @Test
    public void 백테스팅_캔들_로딩_테스트() {

        //given
        String keyPairId = UUID.randomUUID().toString();
        String userId = "123456";
        given(exchangeKeyService.findByPairId(keyPairId))
            .willReturn(
                List.of(
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.UPBIT)
                        .name("ACCESS_KEY")
                        .value(accessKey)
                        .userId(userId)
                        .build(),
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.UPBIT)
                        .name("SECRET_KEY")
                        .value(secretKey)
                        .userId(userId)
                        .build()
                )
            );

        LocalDateTime start = LocalDateTime.of(2022, 03, 25, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 03, 25, 14, 6, 14);
        BackTestingCandleLoader backTestingCandleLoader = BackTestingCandleLoader.builder()
            .coinType(CoinType.ETHEREUM)
            .keyPairdId(keyPairId)
            .spotExchangeCandleService(exchangeCandleService)
            .start(start)
            .end(end)
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
}