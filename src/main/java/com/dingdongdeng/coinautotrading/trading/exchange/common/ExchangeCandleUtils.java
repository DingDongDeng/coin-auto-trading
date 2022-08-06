package com.dingdongdeng.coinautotrading.trading.exchange.common;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExchangeCandleUtils {

    private final int MAX_CHUNK_SIZE;

    public LocalDateTime getlimitedEndDateTime(CandleUnit candleUnit, LocalDateTime start, LocalDateTime end) {
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

    public LocalDateTime getlimitedStartDateTime(CandleUnit candleUnit, LocalDateTime end) {
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

    public int getCandleCount(CandleUnit candleUnit, LocalDateTime start, LocalDateTime limitedEndDateTime) {
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

        if (diff > MAX_CHUNK_SIZE) {
            throw new RuntimeException("candle max size over");
        }
        return diff.intValue();
    }
}
