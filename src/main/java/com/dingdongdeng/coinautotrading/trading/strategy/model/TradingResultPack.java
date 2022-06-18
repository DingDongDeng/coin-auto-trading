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
public class TradingResultPack<T extends TradingResult> {

    private List<T> buyTradingResultList = new ArrayList<>(); // 매수 주문
    private List<T> profitTradingResultList = new ArrayList<>(); // 익절 주문
    private List<T> lossTradingResultList = new ArrayList<>(); // 손절 주문

    public void reset() {
        buyTradingResultList.clear();
        profitTradingResultList.clear();
        lossTradingResultList.clear();
    }

    public void add(T tradingResult) {
        TradingTag tag = tradingResult.getTradingTag();
        findTargetTradingResultList(tag).add(tradingResult);
    }

    public void delete(T tradingResult) {
        List<T> tradingResultList = findTargetTradingResultList(tradingResult.getTradingTag());
        TradingResult targetTradingResult = findTradingResult(tradingResultList, tradingResult.getOrderId());
        tradingResultList.remove(targetTradingResult);
    }

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
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

    private List<T> findTargetTradingResultList(TradingTag tag) {
        List<T> tradingResultList = Map.of(
            TradingTag.BUY, buyTradingResultList,
            TradingTag.PROFIT, profitTradingResultList,
            TradingTag.LOSS, lossTradingResultList
        ).get(tag);
        if (Objects.isNull(tradingResultList)) {
            throw new RuntimeException("Not found tradingResultList");
        }
        return tradingResultList;
    }

    private T findTradingResult(List<T> tradingResultList, String orderId) {
        for (T tradingResult : tradingResultList) {
            if (tradingResult.getOrderId().equals(orderId)) {
                return tradingResult;
            }
        }
        throw new RuntimeException("Not found TradingResult by orderId");
    }

}
