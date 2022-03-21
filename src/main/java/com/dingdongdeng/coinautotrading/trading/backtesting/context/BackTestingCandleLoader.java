package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BackTestingCandleLoader {

    private CoinType coinType;
    private String keyPairdId;
    private ExchangeCandleService exchangeCandleService;
    private int chunkSize;

    @Default
    private CandleUnit BACK_TESTING_CANDLE_UNIT = CandleUnit.UNIT_1M;
    private LocalDateTime start;
    private LocalDateTime end;
    @Default
    private List<Candle> candleList = getCandles(BACK_TESTING_CANDLE_UNIT, start).getCandleList();
    @Default
    private int cursor = -1;

    public Candle getNextCandle() {
        // 다음 캔들을 얻기 위해 커서값을 1 증가 시키고 이를 인덱스로 사용
        int index = ++cursor;

        //현재 읽어온 캔들리스트를 다 읽었다면
        if (candleList.size() - 1 < index) {

            //다음에 새로 읽어야할 캔들리스트가 있는지 확인
            Candle lastCandle = candleList.get(cursor - 1);
            if (lastCandle.getCandleDateTimeKst().isBefore(end)) {

                // 캔들 리스트를 새로 세팅하고, 커서를 초기화
                candleList = getCandles(BACK_TESTING_CANDLE_UNIT, lastCandle.getCandleDateTimeKst()).getCandleList();
                cursor = -1;

                // 새 캔들 리스트의 첫번째 캔들을 반환
                return candleList.get(++cursor);
            }
        }

        //현재 읽어온 캔들리스트를 다 읽지 않았다면
        Candle nextCandle = candleList.get(index);
        if (nextCandle.getCandleDateTimeKst().isBefore(end)) {
            return nextCandle;
        }

        // start, end 사이의 캔들을 모두 읽음
        return null;
    }

    public ExchangeCandles getCandles(CandleUnit candleUnit, LocalDateTime start) {
        return exchangeCandleService
            .getCandles(
                coinType,
                candleUnit,
                start,
                chunkSize,
                keyPairdId);
    }

    public CoinExchangeType getCoinExchangeType() {
        return this.exchangeCandleService.getCoinExchangeType();
    }

}
