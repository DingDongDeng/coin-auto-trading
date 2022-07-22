package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl.BinanceFutureExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitExchangeCandleService;
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
//@SpringBootTest(classes = {
//    UpbitExchangeCandleService.class,
//    UpbitClient.class,
//    UpbitWebClientConfig.class,
//    UpbitClientResourceProperties.class,
//    UpbitTokenGenerator.class,
//    QueryParamsConverter.class,
//    ObjectMapper.class
//}
//)
@SpringBootTest
    //fixme ObjectMapper 주입받다가 의도한대로 동작안함...
class BinanceFutureExchangeCandleServiceTest {

    @MockBean
    private ExchangeKeyService exchangeKeyService;
    @Autowired
    private BinanceFutureExchangeCandleService service;

    @Value("${binance.future.client.accessKey}")
    private String accessKey;
    @Value("${binance.future.client.secretKey}")
    private String secretKey;

    @Test
    public void 캔들_1분봉_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = LocalDateTime.of(2022, 06, 13, 20, 01, 00);
        LocalDateTime end = LocalDateTime.of(2022, 06, 13, 20, 05, 59);
        String keyPairId = UUID.randomUUID().toString();
        String userId = "123456";
        given(exchangeKeyService.findByPairId(keyPairId))
            .willReturn(
                List.of(
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("ACCESS_KEY")
                        .value(accessKey)
                        .userId(userId)
                        .build(),
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("SECRET_KEY")
                        .value(secretKey)
                        .userId(userId)
                        .build()
                )
            );

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(4, candles.getCandleList().size());
        assertEquals(start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
        assertEquals(end.withSecond(0), candles.getCandleList().get(3).getCandleDateTimeKst());
        assertEquals(1223.13, candles.getCandleList().get(0).getTradePrice());
        assertEquals(1225.89, candles.getCandleList().get(3).getTradePrice());
    }

    @Test
    public void 캔들_1분봉_최대치_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = LocalDateTime.of(2022, 06, 11, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 06, 13, 10, 45, 15);
        String keyPairId = UUID.randomUUID().toString();
        String userId = "123456";
        given(exchangeKeyService.findByPairId(keyPairId))
            .willReturn(
                List.of(
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("ACCESS_KEY")
                        .value(accessKey)
                        .userId(userId)
                        .build(),
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("SECRET_KEY")
                        .value(secretKey)
                        .userId(userId)
                        .build()
                )
            );

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(1500, candles.getCandleList().size());
        assertEquals(start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
        assertEquals(start.plusMinutes(1500).withSecond(0), candles.getCandleList().get(1499).getCandleDateTimeKst());
        assertEquals(1676.77, candles.getCandleList().get(0).getTradePrice());
        assertEquals(1458.4, candles.getCandleList().get(1499).getTradePrice());
    }

    @Test
    public void 캔들_1분봉_end_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.of(2022, 06, 13, 10, 45, 0);
        String keyPairId = UUID.randomUUID().toString();
        String userId = "123456";
        given(exchangeKeyService.findByPairId(keyPairId))
            .willReturn(
                List.of(
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("ACCESS_KEY")
                        .value(accessKey)
                        .userId(userId)
                        .build(),
                    ExchangeKey.builder()
                        .pairId(keyPairId)
                        .coinExchangeType(CoinExchangeType.BINANCE_FUTURE)
                        .name("SECRET_KEY")
                        .value(secretKey)
                        .userId(userId)
                        .build()
                )
            );

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(1500, candles.getCandleList().size());
        assertEquals(end.withSecond(0), candles.getCandleList().get(1499).getCandleDateTimeKst());
    }


}