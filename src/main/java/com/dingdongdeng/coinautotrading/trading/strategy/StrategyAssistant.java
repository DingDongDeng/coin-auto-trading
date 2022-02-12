package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyAssistant {

    private final TradingResultRepository tradingResultRepository;

    public void delete(TradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getId())) {
            return;
        }
        tradingResultRepository.delete(tradingResult);
    }

    public void saveTradingResult(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getStrategyName(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    public TradingResult findTradingResult(String strategyName, TradingTag tag) {
        return tradingResultRepository.findById(getKey(strategyName, tag)).orElse(new TradingResult());
    }

    public TradingResult findProfitTradingResult(Class clazz) {
        return findTradingResult(clazz.getSimpleName(), TradingTag.PROFIT);
    }

    public TradingResult findLossTradingResult(Class clazz) {
        return findTradingResult(clazz.getSimpleName(), TradingTag.LOSS);
    }

    public TradingResult findBuyTradingResult(Class clazz) {
        return findTradingResult(clazz.getSimpleName(), TradingTag.BUY);
    }

    public boolean isEnoughBalance(double balance, double accountBalanceLimit) {
        return balance > accountBalanceLimit;
    }

    private String getKey(String strategyName, TradingTag tag) {
        return strategyName + ":" + tag.name(); // RsiTradingStrategy:BUY
    }
}
