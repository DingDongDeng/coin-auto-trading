package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
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
class UpbitSpotExchangeCandleServiceTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitSpotExchangeCandleService service;

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

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = LocalDateTime.of(2022, 03, 27, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 15);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(4, candles.getCandleList().size());
        assertEquals(start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
        assertEquals(end.withSecond(0), candles.getCandleList().get(3).getCandleDateTimeKst());
        assertEquals(3812000, candles.getCandleList().get(0).getTradePrice());
        assertEquals(3811000, candles.getCandleList().get(3).getTradePrice());
    }

    @Test
    public void 캔들_1분봉_최대치_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = LocalDateTime.of(2022, 03, 25, 10, 41, 27);
        LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 15);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(200, candles.getCandleList().size());
        assertEquals(start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
        assertEquals(start.plusMinutes(200).withSecond(0), candles.getCandleList().get(199).getCandleDateTimeKst());
        assertEquals(3763000, candles.getCandleList().get(0).getTradePrice());
        assertEquals(3778000, candles.getCandleList().get(199).getTradePrice());
    }

    @Test
    public void 캔들_1분봉_end_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1M;
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 0);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(200, candles.getCandleList().size());
        assertEquals(end.withSecond(0), candles.getCandleList().get(199).getCandleDateTimeKst());
    }

    @Test
    public void 캔들_일봉_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1D;
        LocalDateTime start = LocalDateTime.of(2022, 8, 4, 21, 35, 27);
        LocalDateTime end = LocalDateTime.of(2022, 8, 7, 21, 37, 15);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(3, candles.getCandleList().size());
        assertEquals(start.plusDays(1).toLocalDate(), candles.getCandleList().get(0).getCandleDateTimeKst().toLocalDate());
        assertEquals(end.toLocalDate(), candles.getCandleList().get(2).getCandleDateTimeKst().toLocalDate());
        assertEquals(2293000, candles.getCandleList().get(0).getTradePrice());
        assertEquals(2254000, candles.getCandleList().get(2).getTradePrice());
    }

    @Test
    public void 캔들_일봉_최대치_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1D;
        LocalDateTime start = LocalDateTime.of(2021, 10, 4, 21, 35, 27);
        LocalDateTime end = LocalDateTime.of(2022, 8, 7, 21, 37, 15);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(200, candles.getCandleList().size());
        assertEquals(start.plusDays(1).toLocalDate(), candles.getCandleList().get(0).getCandleDateTimeKst().toLocalDate());
        assertEquals(start.plusDays(200).toLocalDate(), candles.getCandleList().get(199).getCandleDateTimeKst().toLocalDate());
        assertEquals(4211000, candles.getCandleList().get(0).getTradePrice());
        assertEquals(3720000, candles.getCandleList().get(199).getTradePrice());
    }

    @Test
    public void 캔들_일봉_end_조회_테스트() {

        //given
        CoinType coinType = CoinType.ETHEREUM;
        CandleUnit candleUnit = CandleUnit.UNIT_1D;
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.of(2022, 8, 7, 21, 37, 15);

        //when
        ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

        //then
        assertEquals(200, candles.getCandleList().size());
        assertEquals(end.toLocalDate(), candles.getCandleList().get(199).getCandleDateTimeKst().toLocalDate());
    }


}