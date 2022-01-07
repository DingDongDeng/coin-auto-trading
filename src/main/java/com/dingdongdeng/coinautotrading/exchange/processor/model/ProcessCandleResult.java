package com.dingdongdeng.coinautotrading.exchange.processor.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ProcessCandleResult {

    private CoinType coinType;
    private Integer unit; // 분 단위(유닛)
    private List<ProcessCandle> candleList;


    @ToString
    @Getter
    @Builder
    public static class ProcessCandle {

        private String candleDateTimeUtc; // 캔들 기준 시각(UTC 기준)
        private String candleDateTimeKst; // 캔들 기준 시각(KST 기준)
        private Double openingPrice; // 시가
        private Double highPrice; // 고가
        private Double lowPrice; // 저가
        private Double tradePrice; // 종가
        private Long timestamp; // 해당 캔들에서 마지막 틱이 저장된 시각
        private Double candleAccTradePrice; // 누적 거래 금액
        private Double candleAccTradeVolume; // 누적 거래량
    }
}
