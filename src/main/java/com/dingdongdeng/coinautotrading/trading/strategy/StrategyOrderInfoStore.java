package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StrategyOrderInfoStore {

    private final TradingResultRepository tradingResultRepository;

    public TradingResultPack get(String identifyCode) {
        return TradingResultPack.builder()
            .buyTradingResult(findTradingResult(identifyCode, TradingTag.BUY))
            .profitTradingResult(findTradingResult(identifyCode, TradingTag.PROFIT))
            .lossTradingResult(findTradingResult(identifyCode, TradingTag.LOSS))
            .build();
    }

    public void save(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getIdentifyCode(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    public void reset(String identifyCode) {
        TradingResult buyTradingResult = findTradingResult(identifyCode, TradingTag.BUY);
        TradingResult profitTradingResult = findTradingResult(identifyCode, TradingTag.PROFIT);
        TradingResult lossTradingResult = findTradingResult(identifyCode, TradingTag.LOSS);

        if (buyTradingResult.isExist()) {
            tradingResultRepository.delete(buyTradingResult);
        }
        if (profitTradingResult.isExist()) {
            tradingResultRepository.delete(profitTradingResult);
        }
        if (lossTradingResult.isExist()) {
            tradingResultRepository.delete(lossTradingResult);
        }
    }

    public void reset(TradingResult tradingResult) {
        String identifyCode = tradingResult.getIdentifyCode();
        TradingTag tag = tradingResult.getTag();
        TradingResult storedTradingResult = findTradingResult(identifyCode, tag);
        tradingResultRepository.delete(storedTradingResult);
    }

    private TradingResult findTradingResult(String identifyCode, TradingTag tag) {
        return tradingResultRepository.findById(getKey(identifyCode, tag)).orElse(new TradingResult());
    }

    private String getKey(String identifyCode, TradingTag tag) {
        return identifyCode + ":" + tag.name();
    }

}
