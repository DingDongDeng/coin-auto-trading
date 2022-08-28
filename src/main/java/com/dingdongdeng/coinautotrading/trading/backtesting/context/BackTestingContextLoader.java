package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.VirtualCandle;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            .candles(candles) // tradingTermCandleLoader를 사용해서 효율적으로 캔들 리스트를 생성
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
                candle = tradingTermCandleLoader.getNextCandle();
                tradingTermCandleList.add(candle);
            }

            if (!isExistCurrentCandle(currentTime, tradingTermCandleList)) {
                tradingTermCandleList = this.addVirtualCandle(tradingTermCandleList, currentCandle, this.tradingTermCandleLoader.getCandleUnit());
            }
        } else {
            Candle lastCandle = tradingTermCandleList.get(tradingTermCandleList.size() - 1);
            if (isVirtualCandle(lastCandle)) {
                tradingTermCandleList.remove(lastCandle);
            }

            while (isNeedNextTradingTermCandle(tradingTermCandleList.get(tradingTermCandleList.size() - 1), currentTime)) {
                tradingTermCandleList.add(tradingTermCandleLoader.getNextCandle());
                tradingTermCandleList.remove(0);
            }

            if (!isExistCurrentCandle(currentTime, tradingTermCandleList)) {
                // 현재 시간에 해당하는 캔들이 없어서, 백테스팅에서 시간축으로 사용하는 캔들로 임의 생성
                tradingTermCandleList = this.addVirtualCandle(tradingTermCandleList, currentCandle, this.tradingTermCandleLoader.getCandleUnit());
            }
        }

        while (isNeedNextTradingTermCandle(tradingTermCandleList.get(tradingTermCandleList.size() - 1), currentTime)) {
            tradingTermCandleList.add(tradingTermCandleLoader.getNextCandle());
            tradingTermCandleList.remove(0);
        }

        if (!isExistCurrentCandle(currentTime, tradingTermCandleList)) {
            // 현재 시간에 해당하는 캔들이 없어서, 백테스팅에서 시간축으로 사용하는 캔들로 임의 생성
            tradingTermCandleList = this.addVirtualCandle(tradingTermCandleList, currentCandle, this.tradingTermCandleLoader.getCandleUnit());
        }

        return ExchangeCandles.builder()
            .coinExchangeType(tradingTermCandleLoader.getCoinExchangeType())
            .coinType(tradingTermCandleLoader.getCoinType())
            .candleUnit(tradingTermCandleLoader.getCandleUnit())
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

    private boolean isExistCurrentCandle(LocalDateTime currentTime, List<Candle> candleList) {
        return candleList.get(candleList.size() - 1).getCandleDateTimeKst().equals(currentTime);
    }

    private boolean isVirtualCandle(Candle candle) {
        if (Objects.isNull(candle)) {
            return false;
        }
        return candle instanceof VirtualCandle;
    }

    private List<Candle> addVirtualCandle(List<Candle> candleList, Candle currentCandle, CandleUnit candleUnit) {
        VirtualCandle virtualCandle = VirtualCandle.virtualCandleBuilder()
            .candleDateTimeUtc(currentCandle.getCandleDateTimeUtc())
            .candleDateTimeKst(currentCandle.getCandleDateTimeKst())
            .openingPrice(currentCandle.getOpeningPrice())
            .highPrice(currentCandle.getHighPrice())
            .lowPrice(currentCandle.getLowPrice())
            .tradePrice(currentCandle.getTradePrice())
            .timestamp(currentCandle.getTimestamp())
            .candleAccTradePrice(currentCandle.getCandleAccTradePrice())
            .candleAccTradeVolume(currentCandle.getCandleAccTradeVolume())
            .build();

        switch (candleUnit.getUnitType()) {
            case MIN -> candleList.add(virtualCandle);
            case DAY -> {
                /*
                 * 백테스팅을 실제와 비슷하게 하기 위해서, 가상 캔들을 생성하여 데이터를 구성함
                 * 그런데, 일봉의 경우 가상 캔들의 날짜가 기존 캔들과 중복 되는 케이스가 존재함
                 * 예를 들어, 기존 캔들에 "22.08.13 09:00:00" 가 존재하고, 가상 캔들이 "22.08.13 03:12:00" 이라면
                 *
                 * 같은 날짜에 대해서
                 * */
                Candle lastCandle = candleList.get(candleList.size() - 1);
                LocalDate virtualCandleDate = virtualCandle.getCandleDateTimeKst().toLocalDate();
                LocalDate currentCandleDate = lastCandle.getCandleDateTimeKst().toLocalDate();
                if (virtualCandleDate.equals(currentCandleDate)) {
                    candleList.remove(lastCandle);
                }
                candleList.add(virtualCandle);
            }
        }
        return candleList;
    }

    private List<Candle> removeVirtualCandle(List<Candle> candleList, CandleUnit candleUnit) {
        Candle virtualCandle = candleList.get(candleList.size() - 1);
        switch (candleUnit.getUnitType()) {
            case MIN -> candleList.remove(virtualCandle);
            case DAY -> {
                /*
                 일봉의 경우 백테스팅 데이터를 최대한 실제와 비슷하게 표현하기 위해서 candleList를 가상 캔들로 구성함
                 */
                boolean isMidnight = virtualCandle.getCandleDateTimeKst().toLocalTime().equals(LocalTime.of(0, 0, 0));

            }
        }

        return candleList;
    }
}
