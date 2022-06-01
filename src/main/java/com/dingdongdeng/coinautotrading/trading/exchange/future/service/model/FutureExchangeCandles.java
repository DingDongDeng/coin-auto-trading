package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeCandles {

    private CoinExchangeType coinExchangeType;
    private CoinType coinType;
    private CandleUnit candleUnit; // 분 단위(유닛)
    private List<Candle> candleList;


    @ToString
    @Getter
    @Builder
    public static class Candle {

        private LocalDateTime candleDateTimeUtc; // 캔들 기준 시각(UTC 기준)
        private LocalDateTime candleDateTimeKst; // 캔들 기준 시각(KST 기준)
        private Double openingPrice; // 시가
        private Double highPrice; // 고가
        private Double lowPrice; // 저가
        private Double tradePrice; // 종가
        private Long timestamp; // 해당 캔들에서 마지막 틱이 저장된 시각
        private Double candleAccTradePrice; // 누적 거래 금액
        private Double candleAccTradeVolume; // 누적 거래량
    }

    // 현재 시점으로부터 n번째 과거 캔들을 조회
    public Candle getLatest(int index) {
        if (candleList.size() < index + 1) {
            throw new RuntimeException("Not found candle");
        }
        return candleList.get(candleList.size() - 1 - index);
    }
}
