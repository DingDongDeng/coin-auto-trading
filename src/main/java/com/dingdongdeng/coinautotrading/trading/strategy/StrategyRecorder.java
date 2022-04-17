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
    private double totalPaidFee;
    private double marginPrice;
    private double marginRate;
    private String eventMessage = ""; //fixme 메모리 이슈 가능성이 있음

    public void apply(TradingResult tradingResult) {
        addEventMessage("주문", tradingResult);

        totalPaidFee += tradingResult.getFee();
        TradingTag tag = tradingResult.getTag();
        if (tag == TradingTag.BUY) {
            totalBuyPrice += tradingResult.getPrice() * tradingResult.getVolume();
            return;
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
        addEventMessage("취소", tradingResult);

        totalPaidFee -= tradingResult.getFee();
        TradingTag tag = tradingResult.getTag();
        if (tag == TradingTag.BUY) {
            totalBuyPrice -= tradingResult.getPrice() * tradingResult.getVolume();
            return;
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
        this.marginRate = ((totalProfitPrice + totalLossPrice - totalPaidFee) / totalBuyPrice) * 100d - 100d;
        this.marginPrice = (totalProfitPrice + totalLossPrice - totalPaidFee) - totalBuyPrice;
    }

    private void addEventMessage(String event, TradingResult tradingResult) {
        String tagName = tradingResult.getTag().getDesc();
        double price = tradingResult.getPrice();
        double orderPrice = tradingResult.getPrice() * tradingResult.getVolume();
        this.eventMessage += tradingResult.getCreatedAt() + "/" + event + " / " + tagName + " / " + price + " / " + orderPrice + "</br>\n";
    }
}
