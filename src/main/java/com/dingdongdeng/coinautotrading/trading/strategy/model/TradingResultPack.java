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
            return;
        }
        if (tag == TradingTag.LOSS) {
            lossTradingResultList.add(tradingResult);
            return;
        }
        if (tag == TradingTag.PROFIT) {
            profitTradingResultList.add(tradingResult);
            return;
        }
        throw new RuntimeException("Not found tag");
    }

    public void delete(TradingResult tradingResult) {
        String orderId = tradingResult.getOrderId();
        TradingTag tag = tradingResult.getTag();

        if (tag == TradingTag.BUY) {
            TradingResult targetTradingResult = findTradingResult(buyTradingResultList, orderId);
            buyTradingResultList.remove(targetTradingResult);
            return;
        }
        if (tag == TradingTag.LOSS) {
            TradingResult targetTradingResult = findTradingResult(lossTradingResultList, orderId);
            lossTradingResultList.remove(targetTradingResult);
            return;
        }
        if (tag == TradingTag.PROFIT) {
            TradingResult targetTradingResult = findTradingResult(profitTradingResultList, orderId);
            profitTradingResultList.remove(targetTradingResult);
            return;
        }
        throw new RuntimeException("Not found tag");
    }

    private TradingResult findTradingResult(List<TradingResult> tradingResultList, String orderId) {
        for (TradingResult tradingResult : tradingResultList) {
            if (tradingResult.getOrderId().equals(orderId)) {
                return tradingResult;
            }
        }
        throw new RuntimeException("Not found TradingResult by orderId");
    }

}
