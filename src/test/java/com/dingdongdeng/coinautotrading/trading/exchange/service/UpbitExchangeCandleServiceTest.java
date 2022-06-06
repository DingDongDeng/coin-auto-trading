package com.dingdongdeng.coinautotrading.trading.exchange.service;

// import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.impl.UpbitExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
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
// @SpringBootTest(classes = {
//    UpbitExchangeCandleService.class,
//    UpbitClient.class,
//    UpbitWebClientConfig.class,
//    UpbitClientResourceProperties.class,
//    UpbitTokenGenerator.class,
//    QueryParamsConverter.class,
//    ObjectMapper.class
// }
// )
@SpringBootTest
// fixme ObjectMapper 주입받다가 의도한대로 동작안함...
class UpbitExchangeCandleServiceTest {

  @MockBean private ExchangeKeyService exchangeKeyService;
  @Autowired private UpbitExchangeCandleService service;

  @Value("${upbit.client.accessKey}")
  private String accessKey;

  @Value("${upbit.client.secretKey}")
  private String secretKey;

  @Test
  public void 캔들_1분봉_조회_테스트() {

    // given
    CoinType coinType = CoinType.ETHEREUM;
    CandleUnit candleUnit = CandleUnit.UNIT_1M;
    LocalDateTime start = LocalDateTime.of(2022, 03, 27, 10, 41, 27);
    LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 15);
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
                    .build()));

    // when
    ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

    // then
    assertEquals(4, candles.getCandleList().size());
    assertEquals(
        start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
    assertEquals(end.withSecond(0), candles.getCandleList().get(3).getCandleDateTimeKst());
    assertEquals(3812000, candles.getCandleList().get(0).getTradePrice());
    assertEquals(3811000, candles.getCandleList().get(3).getTradePrice());
  }

  @Test
  public void 캔들_1분봉_최대치_조회_테스트() {

    // given
    CoinType coinType = CoinType.ETHEREUM;
    CandleUnit candleUnit = CandleUnit.UNIT_1M;
    LocalDateTime start = LocalDateTime.of(2022, 03, 25, 10, 41, 27);
    LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 15);
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
                    .build()));

    // when
    ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

    // then
    assertEquals(200, candles.getCandleList().size());
    assertEquals(
        start.plusMinutes(1).withSecond(0), candles.getCandleList().get(0).getCandleDateTimeKst());
    assertEquals(
        start.plusMinutes(200).withSecond(0),
        candles.getCandleList().get(199).getCandleDateTimeKst());
    assertEquals(3763000, candles.getCandleList().get(0).getTradePrice());
    assertEquals(3778000, candles.getCandleList().get(199).getTradePrice());
  }

  @Test
  public void 캔들_1분봉_end_조회_테스트() {

    // given
    CoinType coinType = CoinType.ETHEREUM;
    CandleUnit candleUnit = CandleUnit.UNIT_1M;
    LocalDateTime start = null;
    LocalDateTime end = LocalDateTime.of(2022, 03, 27, 10, 45, 0);
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
                    .build()));

    // when
    ExchangeCandles candles = service.getCandles(coinType, candleUnit, start, end, keyPairId);

    // then
    assertEquals(200, candles.getCandleList().size());
    assertEquals(end.withSecond(0), candles.getCandleList().get(199).getCandleDateTimeKst());
  }
}
