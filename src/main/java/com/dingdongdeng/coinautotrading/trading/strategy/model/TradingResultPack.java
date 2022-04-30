package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
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
        findTargetTradingResultList(tag).add(tradingResult);
    }

    public void delete(TradingResult tradingResult) {
        List<TradingResult> tradingResultList = findTargetTradingResultList(tradingResult.getTag());
        TradingResult targetTradingResult = findTradingResult(tradingResultList, tradingResult.getOrderId());
        tradingResultList.remove(targetTradingResult);
    }

    public List<TradingResult> getAll() {
        List<TradingResult> list = new ArrayList<>();
        list.addAll(buyTradingResultList);
        list.addAll(profitTradingResultList);
        list.addAll(lossTradingResultList);
        return list;
    }

    public double getAveragePrice() {
        double buyValueSum = buyTradingResultList.stream()
            .mapToDouble(tradingResult -> tradingResult.getPrice() * tradingResult.getVolume())
            .sum();
        double profitValueSum = profitTradingResultList.stream()
            .mapToDouble(tradingResult -> tradingResult.getPrice() * tradingResult.getVolume())
            .sum();
        double lossValueSum = lossTradingResultList.stream()
            .mapToDouble(tradingResult -> tradingResult.getPrice() * tradingResult.getVolume())
            .sum();
        return (buyValueSum - profitValueSum - lossValueSum) / getVolume();
    }

    public double getVolume() {
        double buyVolume = getBuyVolume();
        double profitVolume = getProfitVolume();
        double lossVolume = getLossVolume();
        return buyVolume - profitVolume - lossVolume;
    }

    public double getBuyVolume() {
        return buyTradingResultList.stream().mapToDouble(TradingResult::getVolume).sum();
    }

    public double getProfitVolume() {
        return profitTradingResultList.stream().mapToDouble(TradingResult::getVolume).sum();
    }

    public double getLossVolume() {
        return lossTradingResultList.stream().mapToDouble(TradingResult::getVolume).sum();
    }

    private List<TradingResult> findTargetTradingResultList(TradingTag tag) {
        List<TradingResult> tradingResultList = Map.of(
            TradingTag.BUY, buyTradingResultList,
            TradingTag.PROFIT, profitTradingResultList,
            TradingTag.LOSS, lossTradingResultList
        ).get(tag);
        if (Objects.isNull(tradingResultList)) {
            throw new RuntimeException("Not found tradingResultList");
        }
        return tradingResultList;
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
