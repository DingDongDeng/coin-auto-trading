package com.dingdongdeng.coinautotrading.trading.exchange.future.service.impl;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.future.client.BinanceFutureClient;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceFutureExchangeCandleService implements ExchangeCandleService {

    private final CoinExchangeType COIN_EXCHANGE_TYPE = CoinExchangeType.BINANCE_FUTURE;
    private final BinanceFutureClient binanceFutureClient;
    private final int MAX_CHUNK_SIZE = 200;

    @Override
    public ExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId) {
        return null;
    }

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return null;
    }

    /*@Override //fixme 분봉만 지원
    public ExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId) {
        *//**
     * start를 기준으로 최대 캔들 200개까지 조회 가능
     * start < 캔들 <= end 범위로 조회
     * start ~ end 순서로 정렬된 캔들 정보 반환
     * start가 null이라면 end를 기준으로 캔들 200개까지 조회
     *//*
        if (Objects.isNull(start)) {
            start = getlimitedStartDateTime(candleUnit, end);
        }

        LocalDateTime limitedEndDateTime = getlimitedEndDateTime(candleUnit, start, end);
        int candleCount = getCandleCount(candleUnit, start, limitedEndDateTime);
        List<CandleResponse> response = upbitClient.getMinuteCandle(
            CandleRequest.builder()
                .unit(candleUnit.getSize())
                .market(MarketType.of(coinType).getCode())
                .toKst(limitedEndDateTime)
                .count(candleCount)
                .build(),
            keyPairId
        );
        Collections.reverse(response);

        return ExchangeCandles.builder()
            .coinExchangeType(getCoinExchangeType())
            .candleUnit(candleUnit)
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

    @Override
    public CoinExchangeType getCoinExchangeType() {
        return COIN_EXCHANGE_TYPE;
    }

    private LocalDateTime getlimitedEndDateTime(CandleUnit candleUnit, LocalDateTime start, LocalDateTime end) {
        LocalDateTime limitedEndDateTime;
        int unitSize = candleUnit.getSize();
        switch (candleUnit.getUnitType()) {
            case WEEK:
                limitedEndDateTime = start.plusWeeks(MAX_CHUNK_SIZE * unitSize);
                break;
            case DAY:
                limitedEndDateTime = start.plusDays(MAX_CHUNK_SIZE * unitSize);
                break;
            case MIN:
                limitedEndDateTime = start.plusMinutes(MAX_CHUNK_SIZE * unitSize);
                break;
            default:
                throw new NoSuchElementException("fail make limitedEndDateTime");
        }
        return (end.isAfter(limitedEndDateTime) ? limitedEndDateTime : end).withSecond(59);
    }

    private LocalDateTime getlimitedStartDateTime(CandleUnit candleUnit, LocalDateTime end) {
        LocalDateTime limitedStartDateTime;
        int unitSize = candleUnit.getSize();
        switch (candleUnit.getUnitType()) {
            case WEEK:
                limitedStartDateTime = end.minusWeeks(MAX_CHUNK_SIZE * unitSize);
                break;
            case DAY:
                limitedStartDateTime = end.minusDays(MAX_CHUNK_SIZE * unitSize);
                break;
            case MIN:
                limitedStartDateTime = end.minusMinutes(MAX_CHUNK_SIZE * unitSize);
                break;
            default:
                throw new NoSuchElementException("fail make limitedStartDateTime");
        }
        return limitedStartDateTime;
    }

    private int getCandleCount(CandleUnit candleUnit, LocalDateTime start, LocalDateTime limitedEndDateTime) {
        Long diff = null;
        switch (candleUnit.getUnitType()) {
            case WEEK:
                diff = ChronoUnit.WEEKS.between(start, limitedEndDateTime) / candleUnit.getSize();
                break;
            case DAY:
                diff = ChronoUnit.DAYS.between(start, limitedEndDateTime) / candleUnit.getSize();
                break;
            case MIN:
                diff = ChronoUnit.MINUTES.between(start, limitedEndDateTime) / candleUnit.getSize();
                break;
            default:
                throw new NoSuchElementException("fail make candleCount");
        }

        if (diff > 200) {
            throw new RuntimeException("upbit candle max size over");
        }
        return diff.intValue();
    }*/
}
