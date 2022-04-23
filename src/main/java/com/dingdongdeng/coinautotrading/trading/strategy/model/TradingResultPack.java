package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradingResultPack {

    private List<TradingResult> buyTradingResultList = new ArrayList<>(); // 매수 주문
    private List<TradingResult> profitTradingResultList = new ArrayList<>(); // 익절 주문
    private List<TradingResult> lossTradingResultList = new ArrayList<>(); // 손절 주문

    public void reset() {
        buyTradingResultList.clear();
        profitTradingResultList.clear();
        lossTradingResultList.clear();
    }

    public void add(TradingResult tradingResult) {
        TradingTag tag = tradingResult.getTag();
        if (tag == TradingTag.BUY) {
            buyTradingResultList.add(tradingResult);
        }
        if (tag == TradingTag.LOSS) {
            lossTradingResultList.add(tradingResult);
        }
        if (tag == TradingTag.PROFIT) {
            profitTradingResultList.add(tradingResult);
        }
    }
}
