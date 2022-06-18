package com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeCandleUtils;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Interval;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureEnum.Symbol;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureRequest.FutureCandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.model.BinanceFutureResponse.FutureCandleResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceFutureExchangeCandleService implements ExchangeCandleService {

    private final CoinExchangeType COIN_EXCHANGE_TYPE = CoinExchangeType.BINANCE_FUTURE;
    private final BinanceFutureClient binanceFutureClient;
    private final int MAX_CHUNK_SIZE = 1500;
    private final ExchangeCandleUtils candleUtils = new ExchangeCandleUtils(MAX_CHUNK_SIZE);


    @Override //fixme 분봉만 지원
    public ExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId) {
        /**
         * start를 기준으로 최대 캔들 1500개까지 조회 가능
         * start < 캔들 <= end 범위로 조회
         * start ~ end 순서로 정렬된 캔들 정보 반환
         * start가 null이라면 end를 기준으로 캔들 1500개까지 조회
         */
        if (Objects.isNull(start)) {
            start = candleUtils.getlimitedStartDateTime(candleUnit, end);
        }

        LocalDateTime limitedEndDateTime = candleUtils.getlimitedEndDateTime(candleUnit, start, end);
        int candleCount = candleUtils.getCandleCount(candleUnit, start, limitedEndDateTime);
        List<FutureCandleResponse> response = binanceFutureClient.getMinuteCandle(
            FutureCandleRequest.builder()
                .interval(Interval.of(candleUnit).getCode())
                .symbol(Symbol.of(coinType).getCode())
                .endTime(convertTimeStamp(limitedEndDateTime))
                .limit(candleCount)
                .build()
        );

        return ExchangeCandles.builder()
            .coinExchangeType(getCoinExchangeType())
            .candleUnit(candleUnit)
            .coinType(coinType)
            .candleList(
                response.stream().map(
                    candle -> ExchangeCandles.Candle.builder()
                        .candleDateTimeUtc(convertLocalDateTime(candle.getOpenTime()))
                        .candleDateTimeKst(convertLocalDateTime(candle.getOpenTime()))
                        .openingPrice(candle.getOpen())
                        .highPrice(candle.getHigh())
                        .lowPrice(candle.getLow())
                        .tradePrice(candle.getClose())
                        .timestamp(candle.getCloseTime())
                        .candleAccTradePrice(candle.getQuoteAssetVolume())
                        .candleAccTradeVolume(candle.getVolume())
                        .build()
                ).collect(Collectors.toList())
            )
            .build();
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return COIN_EXCHANGE_TYPE;
    }

    private Long convertTimeStamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime();
    }

    private LocalDateTime convertLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }
}
