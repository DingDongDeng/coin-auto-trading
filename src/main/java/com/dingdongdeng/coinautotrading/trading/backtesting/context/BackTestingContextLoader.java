package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackTestingContextLoader {

    /*
     백테스팅 상에서 현재 시간에 대한 정보를 담고 있는 캔들을 조회하기 위함
     예를 들어, 1분 단위 백테스팅을 한다면 1분봉 캔들을 순차 조회함
     */
    private final BackTestingCandleLoader currentCandleLoader;

    /*
     백테스팅 상에서 현재 시점의 지표 계산을 위해 활용할 캔들을 조회하기 위함
     예를 들어, 15분봉 기반 지표를 계산하기 위해 15분봉 캔들을 순차 조회함
     */
    private final BackTestingCandleLoader tradingTermCandleLoader;

    /*
    백테스팅 상에서 현재 시점의 캔들 정보들을 임시적으로 저장하고 이후에 각종 지표를 계산하기 위함
    예를 들어, 백테스팅을 위한 가상 캔들을 만들때 1분봉 200개를 조합하면 200분 동안의 누적거래량, 누적 거래금액을 알 수 있음
     */
    private final BackTestingTradingTermCalculator tradingTermCalculator;

    public BackTestingContextLoader(BackTestingCandleLoader currentCandleLoader, BackTestingCandleLoader tradingTermCandleLoader) {
        this.currentCandleLoader = currentCandleLoader;
        this.tradingTermCandleLoader = tradingTermCandleLoader;
        this.tradingTermCalculator = new BackTestingTradingTermCalculator(this.tradingTermCandleLoader.getCandleUnit());
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private BackTestingContext currentContext;

    public boolean hasNext() {
        BackTestingContext context = getNextContext();
        this.setCurrentContext(context);
        return Objects.nonNull(context);
    }

    private BackTestingContext getNextContext() {

        // 현재 정보를 위한 캔들 생성
        Candle currentCandle = this.getCurrentCandle();
        if (Objects.isNull(currentCandle)) {
            return null;
        }

        // 현재 지표 정보를 위한 캔들 생성
        List<Candle> tradingTermCandleList = Optional.ofNullable(currentContext)
            .map(BackTestingContext::getCandles)
            .map(ExchangeCandles::getCandleList)
            .orElse(new ArrayList<>());
        ExchangeCandles candles = this.getCandles(currentCandle, tradingTermCandleList);

        // 컨텍스트 모델 생성
        return BackTestingContext.builder()
            .coinExchangeType(this.currentCandleLoader.getCoinExchangeType())
            .coinType(this.currentCandleLoader.getCoinType())
            .currentPrice(currentCandle.getTradePrice())
            .now(currentCandle.getCandleDateTimeKst())
            .currentCandle(currentCandle)
            .candles(candles)
            .build();
    }

    private Candle getCurrentCandle() {
        return this.currentCandleLoader.getNextCandle();
    }

    private ExchangeCandles getCandles(Candle currentCandle, List<Candle> tradingTermCandleList) {

        LocalDateTime currentTime = currentCandle.getCandleDateTimeKst();

        // 초기화(최초 한번만 실행됨)
        if (tradingTermCandleList.isEmpty()) {
            Candle candle = null;
            while (isNeedNextTradingTermCandle(candle, currentTime)) {
                candle = this.tradingTermCandleLoader.getNextCandle();
                tradingTermCandleList.add(candle);
            }
        }

        // 오래된 캔들을 제거하고, 새로운 캔들을 추가함
        else {
            Candle lastCandle = tradingTermCandleList.get(tradingTermCandleList.size() - 1);
            // 새로운 캔들 추가
            while (isNeedNextTradingTermCandle(lastCandle, currentTime)) {
                lastCandle = this.tradingTermCandleLoader.getNextCandle();
                tradingTermCandleList.add(lastCandle);
            }
            // 오래된 캔들 제거
            while (tradingTermCandleList.size() > 200) {
                tradingTermCandleList.remove(0);
            }
        }

        // 모델 생성
        return ExchangeCandles.builder()
            .coinExchangeType(this.tradingTermCandleLoader.getCoinExchangeType())
            .coinType(this.tradingTermCandleLoader.getCoinType())
            .candleUnit(this.tradingTermCandleLoader.getCandleUnit())
            .candleList(tradingTermCandleList)
            .build();
    }

    private boolean isNeedNextTradingTermCandle(Candle candle, LocalDateTime currentTime) {
        if (Objects.isNull(candle)) {
            return true;
        }

        LocalDateTime candleDateTime = candle.getCandleDateTimeKst();
        CandleUnit candleUnit = tradingTermCandleLoader.getCandleUnit();
        UnitType unitType = candleUnit.getUnitType();
        if (unitType == UnitType.MIN) {
            return candleDateTime.plusMinutes(candleUnit.getSize()).isBefore(currentTime);
        } else if (unitType == UnitType.DAY) {
            return candleDateTime.plusDays(candleUnit.getSize()).isBefore(currentTime);
        } else if (unitType == UnitType.WEEK) {
            return candleDateTime.plusWeeks(candleUnit.getSize()).isBefore(currentTime);
        } else {
            throw new RuntimeException("not found allow unitType");
        }
    }
}
