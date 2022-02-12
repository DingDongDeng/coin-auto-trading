package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitExchangeCandleService implements ExchangeCandleService {

    private final CoinExchangeType COIN_EXCHANGE_TYPE = CoinExchangeType.UPBIT;
    private final UpbitClient upbitClient;
    private final long MAX_COUNT_SIZE = 200;

    @Override
    public ExchangeCandles getCandleList(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end) {
        LocalDateTime limitedEndDateTime = getlimitedEndDateTime(candleUnit, start, end);
        int candleCount = getCandleCount(candleUnit, start, limitedEndDateTime);

        List<CandleResponse> response = upbitClient.getMinuteCandle(
            CandleRequest.builder()
                .unit(candleUnit.getSize())
                .market(MarketType.of(coinType).getCode())
                .toKst(limitedEndDateTime)
                .count(candleCount)
                .build()
        );
        return ExchangeCandles.builder()
            .coinExchangeType(COIN_EXCHANGE_TYPE)
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
        switch (candleUnit.getUnitType()) {
            case WEEK:
                limitedEndDateTime = start.plusWeeks(MAX_COUNT_SIZE);
                break;
            case DAY:
                limitedEndDateTime = start.plusDays(MAX_COUNT_SIZE);
                break;
            case MIN:
                limitedEndDateTime = start.plusMinutes(MAX_COUNT_SIZE);
                break;
            default:
                throw new NoSuchElementException("fail make limitedEndDateTime");
        }
        return end.isAfter(limitedEndDateTime) ? limitedEndDateTime : end;
    }


    private int getCandleCount(CandleUnit candleUnit, LocalDateTime start, LocalDateTime limitedEndDateTime) {
        Long diff = null;
        switch (candleUnit.getUnitType()) {
            case WEEK:
                diff = ChronoUnit.WEEKS.between(start, limitedEndDateTime);
                break;
            case DAY:
                diff = ChronoUnit.DAYS.between(start, limitedEndDateTime);
                break;
            case MIN:
                diff = ChronoUnit.MINUTES.between(start, limitedEndDateTime);
                if (limitedEndDateTime.getSecond() == 0) {
                    diff = diff - 1;
                }
                break;
            default:
                throw new NoSuchElementException("fail make candleCount");
        }

        if (diff > 200) {
            throw new RuntimeException("upbit candle max size over");
        }
        return diff.intValue();
    }
}
