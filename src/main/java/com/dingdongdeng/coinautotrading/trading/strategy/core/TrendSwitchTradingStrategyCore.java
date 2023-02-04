package com.dingdongdeng.coinautotrading.trading.strategy.core;

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

    private final BBandsTradingStrategyCore bBandsTradingStrategyCore;
    private final ResistanceTradingStrategyCore resistanceTradingStrategyCore;

    public TrendSwitchTradingStrategyCore(TrendSwitchTradingStrategyCoreParam param) {
        this.param = param;
        this.bBandsTradingStrategyCore = new BBandsTradingStrategyCore(
            BBandsTradingStrategyCoreParam.builder()
                .initOrderPrice(param.getInitOrderPrice())
                .conditionTimeBuffer(param.getConditionTimeBuffer())
                .accountBalanceLimit(param.getAccountBalanceLimit())
                .tooOldOrderTimeSeconds(param.getTooOldOrderTimeSeconds())
                .processDuration(param.getProcessDuration())
                .build()
        );
        this.resistanceTradingStrategyCore = new ResistanceTradingStrategyCore(
            ResistanceTradingStrategyCoreParam.builder()
                .initOrderPrice(param.getInitOrderPrice())
                .resistancePriceBuffer(param.getConditionTimeBuffer())
                .accountBalanceLimit(param.getAccountBalanceLimit())
                .tooOldOrderTimeSeconds(param.getTooOldOrderTimeSeconds())
                .processDuration(param.getProcessDuration())
                .build()
        );
    }

    @Override
    public List<TradingTask> makeTradingTask(SpotTradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack) {
        double currentPrice = tradingInfo.getCurrentPrice();
        Index index = tradingInfo.getIndex();
        double sma200 = index.getMa().getSma200();

        // 상승장이라면
        if (sma200 < currentPrice) {
            log.info("[분기 조건] 상승 추세");
            return resistanceTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        } else {
            log.info("[분기 조건] 하락 및 횡보 추세");
            return bBandsTradingStrategyCore.makeTradingTask(tradingInfo, tradingResultPack);
        }
    }

    @Override
    public void handleOrderResult(SpotTradingResult tradingResult) {
        resistanceTradingStrategyCore.handleOrderResult(tradingResult);
        bBandsTradingStrategyCore.handleOrderResult(tradingResult);
    }

    @Override
    public void handleOrderCancelResult(SpotTradingResult tradingResult) {
        resistanceTradingStrategyCore.handleOrderCancelResult(tradingResult);
        bBandsTradingStrategyCore.handleOrderCancelResult(tradingResult);
    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }
}
