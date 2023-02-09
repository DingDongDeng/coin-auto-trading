package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles.Candle;
import com.dingdongdeng.coinautotrading.trading.index.Index;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TrendSwitchTradingStrategyCore implements StrategyCore<SpotTradingInfo, SpotTradingResult> {

    private final TrendSwitchTradingStrategyCoreParam param;

    private final MacdTradingStrategyCore macdTradingStrategyCore;
    private final OptimisticBBandsTradingStrategyCore optimisticBBandsTradingStrategyCore;
    private final PessimisticBBandsTradingStrategyCore pessimisticBBandsTradingStrategyCore;

    private Map<String, Trend> orderTrendMap = new HashMap<>();

    public TrendSwitchTradingStrategyCore(TrendSwitchTradingStrategyCoreParam param) {
        this.param = param;
        this.macdTradingStrategyCore = new MacdTradingStrategyCore(
            MacdTradingStrategyCoreParam.builder()
                .initOrderPrice(param.getInitOrderPrice())
                .conditionTimeBuffer(param.getConditionTimeBuffer())
                .accountBalanceLimit(param.getAccountBalanceLimit())
                .tooOldOrderTimeSeconds(param.getTooOldOrderTimeSeconds())
                .processDuration(param.getProcessDuration())
                .build()
        );
        this.optimisticBBandsTradingStrategyCore = new OptimisticBBandsTradingStrategyCore(
            OptimisticBBandsTradingStrategyCoreParam.builder()
                .initOrderPrice(param.getInitOrderPrice())
                .conditionTimeBuffer(param.getConditionTimeBuffer())
                .accountBalanceLimit(param.getAccountBalanceLimit())
                .tooOldOrderTimeSeconds(param.getTooOldOrderTimeSeconds())
                .processDuration(param.getProcessDuration())
                .build()
        );
        this.pessimisticBBandsTradingStrategyCore = new PessimisticBBandsTradingStrategyCore(
            PessimisticBBandsTradingStrategyCoreParam.builder()
                .initOrderPrice(param.getInitOrderPrice())
                .conditionTimeBuffer(param.getConditionTimeBuffer())
                .accountBalanceLimit(param.getAccountBalanceLimit())
                .tooOldOrderTimeSeconds(param.getTooOldOrderTimeSeconds())
                .processDuration(param.getProcessDuration())
                .build()
        );
    }

    @Override
    public List<TradingTask> makeTradingTask(SpotTradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack) {
        Index index = tradingInfo.getIndex();
        ExchangeCandles candles = tradingInfo.getCandles();

        // 매수 주문된게 없음
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            this.orderTrendMap.clear();
        }
        // 이미 매수 주문이 되어있다면
        else {
            SpotTradingResult buyTradingResult = tradingResultPack.getBuyTradingResultList().get(0);
            Trend trend = this.orderTrendMap.get(buyTradingResult.getOrderId());

            if (trend == Trend.BOX) {
                log.info("[분기 조건] 횡보 추세");
                return optimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
            } else if (trend == Trend.UP) {
                log.info("[분기 조건] 상승 추세");
                return macdTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
            } else {
                log.info("[분기 조건] 하락 추세");
                return pessimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
            }
        }

        // 횡보장이라면
        if (isBoxTrend(index, candles)) {
            log.info("[분기 조건] 횡보 추세");
            return optimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
        // 상승장이라면
        else if (isUptrend(index, candles)) {
            log.info("[분기 조건] 상승 추세");
            return macdTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
        // 하락장이라면
        else {
            log.info("[분기 조건] 하락 추세");
            return pessimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
    }

    @Override
    public void handleOrderResult(SpotTradingInfo tradingInfo, SpotTradingResult tradingResult) {
        macdTradingStrategyCore.handleOrderResult(tradingInfo, tradingResult);
        optimisticBBandsTradingStrategyCore.handleOrderResult(tradingInfo, tradingResult);
        pessimisticBBandsTradingStrategyCore.handleOrderResult(tradingInfo, tradingResult);

        Index index = tradingInfo.getIndex();
        ExchangeCandles candles = tradingInfo.getCandles();
        // 횡보장이라면
        if (isBoxTrend(index, candles)) {
            this.orderTrendMap.put(tradingResult.getOrderId(), Trend.BOX);
        }
        // 상승장이라면
        else if (isUptrend(index, candles)) {
            this.orderTrendMap.put(tradingResult.getOrderId(), Trend.UP);
        }
        // 하락장이라면
        else {
            this.orderTrendMap.put(tradingResult.getOrderId(), Trend.DOWN);
        }
    }

    @Override
    public void handleOrderCancelResult(SpotTradingInfo tradingInfo, SpotTradingResult tradingResult) {
        macdTradingStrategyCore.handleOrderCancelResult(tradingInfo, tradingResult);
        optimisticBBandsTradingStrategyCore.handleOrderCancelResult(tradingInfo, tradingResult);
        pessimisticBBandsTradingStrategyCore.handleOrderCancelResult(tradingInfo, tradingResult);

        this.orderTrendMap.remove(tradingResult.getOrderId());
    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }

    private boolean isBoxTrend(Index index, ExchangeCandles candles) {
        double movingAveragePrice = (candles.getLatest(0).getTradePrice() + candles.getLatest(0).getOpeningPrice()) / 2;
        double resistancePrice = index.getResistance().getResistancePrice(movingAveragePrice);
        double supportPrice = index.getResistance().getSupportPrice(movingAveragePrice);

        // 저항선이 없음
        if (resistancePrice == Integer.MAX_VALUE) {
            return false;
        }

        // 지지선이 없음
        if (supportPrice == 0) {
            return false;
        }

        return true;
    }

    private boolean isUptrend(Index index, ExchangeCandles candles) {

        // 최근 캔들이 이평선보다 위인지 확인
        int TARGET_CANDLE_COUNT = 5;
        List<Candle> candleList = candles.getCandleList();
        double sma120 = index.getMa().getSma120();
        int offset = candleList.size() - TARGET_CANDLE_COUNT;
        for (int i = offset; i < candleList.size(); i++) {
            if (candleList.get(i).getTradePrice() < sma120) {
                return false;
            }
        }

        return true;
    }

    private enum Trend {
        UP, DOWN, BOX
    }
}
