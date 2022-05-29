package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BackTestingCandleLoader {

    private CoinType coinType;
    private String keyPairdId;
    private ExchangeCandleService exchangeCandleService;

    @Default
    private CandleUnit candleUnit = CandleUnit.UNIT_1M;
    private LocalDateTime start;
    private LocalDateTime end;

    private List<Candle> candleList;
    @Default
    private int cursor = -1;

    public Candle getNextCandle() {

        // candleList 초기화
        if (Objects.isNull(candleList)) {
            this.candleList = getCandles(candleUnit, start, end).getCandleList();
        }

        // 다음 캔들을 얻기 위해 커서값을 1 증가 시키고 이를 인덱스로 사용
        int index = ++cursor;

        //현재 읽어온 캔들리스트를 다 읽었다면
        if (candleList.size() - 1 < index) {

            //다음에 새로 읽어야할 캔들리스트가 있는지 확인
            Candle lastCandle = candleList.get(cursor - 1);
            if (lastCandle.getCandleDateTimeKst().isBefore(end)) { //fixme 초 떄문에 한번 더 조회됨 해결좀

                // 캔들 리스트를 새로 세팅하고, 커서를 초기화
                candleList = getCandles(candleUnit, lastCandle.getCandleDateTimeKst(), end).getCandleList();
                cursor = -1;

                // 새 캔들 리스트의 첫번째 캔들을 반환
                return candleList.get(++cursor);
            }

            // start, end 사이의 캔들을 모두 읽음
            return null;
        }

        //현재 읽어온 캔들리스트를 다 읽지 않았다면
        Candle nextCandle = candleList.get(index);
        if (nextCandle.getCandleDateTimeKst().isBefore(end) || nextCandle.getCandleDateTimeKst().equals(end)) {
            return nextCandle;
        }

        // start, end 사이의 캔들을 모두 읽음
        return null;
    }

    private ExchangeCandles getCandles(CandleUnit candleUnit, LocalDateTime start, LocalDateTime end) {
        delay(); // candle 조회 api 호출 제한이 걸리지 않도록 하기 위함
        return exchangeCandleService.getCandles(coinType, candleUnit, start, end, keyPairdId);
    }

    public CoinExchangeType getCoinExchangeType() {
        return this.exchangeCandleService.getCoinExchangeType();
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
