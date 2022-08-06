package com.dingdongdeng.coinautotrading.trading.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.UpbitClient;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.CandleResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@Slf4j
@SpringBootTest
class IndexCalculatorTest {

    @Autowired
    private IndexCalculator calculator;
    @MockBean
    private ExchangeKeyService exchangeKeyService;
    @Autowired
    private UpbitClient upbitClient;

    @Value("${upbit.client.accessKey}")
    private String accessKey;
    @Value("${upbit.client.secretKey}")
    private String secretKey;

    private final String keyPairId = UUID.randomUUID().toString();

    @BeforeEach
    public void init() {
        //given
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
    }

    @Test
    public void 저항선_계산_테스트() {
        //given
        CoinType coinType = CoinType.ETHEREUM;
        TradingTerm tradingTerm = TradingTerm.SCALPING;
        LocalDateTime now = LocalDateTime.of(2022, 07, 24, 17, 30, 10);
        ExchangeCandles candles = getExchangeCandles(now, tradingTerm, coinType, keyPairId);

        //when
        List<Double> resistancePriceList = calculator.getResistancePrice(candles);

        //then
        log.info("resistancePrice List = {}", resistancePriceList);
    }

    @Test
    public void RSI_계산_테스트() {
        // given
        CoinType coinType = CoinType.ETHEREUM;
        TradingTerm tradingTerm = TradingTerm.SCALPING;
        LocalDateTime now = LocalDateTime.of(2022, 04, 17, 13, 13, 10);
        ExchangeCandles candles = getExchangeCandles(now, tradingTerm, coinType, keyPairId);

        // when
        double rsi = calculator.getRsi(candles);

        // then
        log.info("result : {}", rsi);
        assertEquals(rsi, 0.39407307248496637);

    }

    @Test
    public void MACD_계산_테스트() {
        // given
        CoinType coinType = CoinType.ETHEREUM;
        TradingTerm tradingTerm = TradingTerm.SCALPING;
        LocalDateTime now = LocalDateTime.of(2022, 8, 6, 9, 45, 10);
        ExchangeCandles candles = getExchangeCandles(now, tradingTerm, coinType, keyPairId);

        // when
        double macd = calculator.getMACD(candles);

        // then
        log.info("result : {}", macd);
        assertEquals(macd, 3399.4713725503443);
    }

    private ExchangeCandles getExchangeCandles(LocalDateTime now, TradingTerm tradingTerm, CoinType coinType, String keyPairId) {
        List<CandleResponse> response = upbitClient.getMinuteCandle(
            CandleRequest.builder()
                .unit(tradingTerm.getCandleUnit().getSize())
                .market(MarketType.of(coinType).getCode())
                .toKst(now)
                .count(200)
                .build(),
            keyPairId
        );
        Collections.reverse(response);
        return ExchangeCandles.builder()
            .coinExchangeType(CoinExchangeType.UPBIT)
            .candleUnit(tradingTerm.getCandleUnit())
            .coinType(coinType)
            .candleList(
                response.stream().map(
                    candle -> ExchangeCandles.Candle.builder()
                        .candleDateTimeUtc(candle.getCandleDateTimeUtc())
                        .candleDateTimeKst(candle.getCandleDateTimeKst())
                        .openingPrice(candle.getOpeningPrice())
                        .highPrice(candle.getHighPrice())
                        .lowPrice(candle.getLowPrice())
                        .tradePrice(candle.getTradePrice())
                        .timestamp(candle.getTimestamp())
                        .candleAccTradePrice(candle.getCandleAccTradePrice())
                        .candleAccTradeVolume(candle.getCandleAccTradeVolume())
                        .build()
                ).collect(Collectors.toList())
            )
            .build();
    }
}