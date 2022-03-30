package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class StrategyRecorder {

    private double totalBuyPrice;
    private double totalProfitPrice;
    private double totalLossPrice;
    private double marginPrice;
    private double marginRate;

    public void apply(TradingResult tradingResult) {
        TradingTag tag = tradingResult.getTag();
        if (tag == TradingTag.BUY) {
            totalBuyPrice += tradingResult.getPrice() * tradingResult.getVolume();
        }
        if (tag == TradingTag.PROFIT) {
            totalProfitPrice += tradingResult.getPrice() * tradingResult.getVolume();
        }
        if (tag == TradingTag.LOSS) {
            totalLossPrice += tradingResult.getPrice() * tradingResult.getVolume();
        }
        calcMargin();
    }

    public void revert(TradingResult tradingResult) {
        TradingTag tag = tradingResult.getTag();
        if (tag == TradingTag.BUY) {
            totalBuyPrice -= tradingResult.getPrice() * tradingResult.getVolume();
        }
        if (tag == TradingTag.PROFIT) {
            totalProfitPrice -= tradingResult.getPrice() * tradingResult.getVolume();
        }
        if (tag == TradingTag.LOSS) {
            totalLossPrice -= tradingResult.getPrice() * tradingResult.getVolume();
        }
        calcMargin();
    }

    private void calcMargin() {
        this.marginRate = (totalProfitPrice + totalLossPrice) / totalBuyPrice;
        this.marginPrice = totalBuyPrice - (totalProfitPrice + totalLossPrice);
    }
}
