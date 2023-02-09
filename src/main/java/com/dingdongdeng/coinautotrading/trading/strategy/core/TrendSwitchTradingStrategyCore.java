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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TrendSwitchTradingStrategyCore implements StrategyCore<SpotTradingInfo, SpotTradingResult> {

    private final TrendSwitchTradingStrategyCoreParam param;

    private final MacdTradingStrategyCore macdTradingStrategyCore;
    private final OptimisticBBandsTradingStrategyCore optimisticBBandsTradingStrategyCore;
    private final PessimisticBBandsTradingStrategyCore pessimisticBBandsTradingStrategyCore;

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

        // 상승장이라면
        if (isUptrend(index, candles)) {
            log.info("[분기 조건] 상승 추세");
            return macdTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
        // 하락장이라면
        else if (isDowntrend(index, candles)) {
            log.info("[분기 조건] 하락 추세");
            return pessimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
        // 횡보장이라면
        else {
            log.info("[분기 조건] 횡보 추세");
            return optimisticBBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
    }

    @Override
    public void handleOrderResult(SpotTradingResult tradingResult) {
        macdTradingStrategyCore.handleOrderResult(tradingResult);
        optimisticBBandsTradingStrategyCore.handleOrderResult(tradingResult);
        pessimisticBBandsTradingStrategyCore.handleOrderResult(tradingResult);
    }

    @Override
    public void handleOrderCancelResult(SpotTradingResult tradingResult) {
        macdTradingStrategyCore.handleOrderCancelResult(tradingResult);
        optimisticBBandsTradingStrategyCore.handleOrderCancelResult(tradingResult);
        pessimisticBBandsTradingStrategyCore.handleOrderCancelResult(tradingResult);
    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }

    private boolean isUptrend(Index index, ExchangeCandles candles) {
        int TARGET_CANDLE_COUNT = 10;
        List<Candle> candleList = candles.getCandleList();
        double sma120 = index.getMa().getSma120();

        // 최근 캔들이 이평선보다 위인지 확인
        int offset = candleList.size() - TARGET_CANDLE_COUNT;
        for (int i = offset; i < candleList.size(); i++) {
            if (candleList.get(i).getTradePrice() < sma120) {
                return false;
            }
        }

        // 이평선 기울기 확인
        double[] sma120s = index.getMa().getSma120s();
        double from = sma120s[sma120s.length - 15];
        double to = sma120s[sma120s.length - 1];
        if (from * (1 + 0.005) > to) {
            return false;
        }

        return true;
    }

    private boolean isDowntrend(Index index, ExchangeCandles candles) {
        int TARGET_CANDLE_COUNT = 10;
        List<Candle> candleList = candles.getCandleList();
        double sma120 = index.getMa().getSma120();

        // 최근 캔들이 이평선보다 위인지 확인
        int offset = candleList.size() - TARGET_CANDLE_COUNT;
        for (int i = offset; i < candleList.size(); i++) {
            if (candleList.get(i).getTradePrice() > sma120) {
                return false;
            }
        }

        // 이평선 기울기 확인
        double[] sma120s = index.getMa().getSma120s();
        double from = sma120s[sma120s.length - 15];
        double to = sma120s[sma120s.length - 1];
        if (from * (1 - 0.005) < to) {
            return false;
        }

        return true;
    }
}
