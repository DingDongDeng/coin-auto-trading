package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyAssistant {

    private final TradingResultRepository tradingResultRepository;

    public TradingResultPack syncedTradingResultPack(StrategyCode code) {
        return TradingResultPack.builder()
            .buyTradingResult(findTradingResult(code, TradingTag.BUY))
            .profitTradingResult(findTradingResult(code, TradingTag.PROFIT))
            .lossTradingResult(findTradingResult(code, TradingTag.LOSS))
            .build();
    }

    public void reset(StrategyCode code) {
        TradingResult buyTradingResult = findTradingResult(code, TradingTag.BUY);
        TradingResult profitTradingResult = findTradingResult(code, TradingTag.PROFIT);
        TradingResult lossTradingResult = findTradingResult(code, TradingTag.LOSS);

        tradingResultRepository.delete(buyTradingResult);
        tradingResultRepository.delete(profitTradingResult);
        tradingResultRepository.delete(lossTradingResult);
    }

    public void storeTradingResult(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getStrategyCode(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    private TradingResult findTradingResult(StrategyCode code, TradingTag tag) {
        return tradingResultRepository.findById(getKey(code, tag)).orElse(new TradingResult());
    }

    private String getKey(StrategyCode code, TradingTag tag) {
        return code.name() + ":" + tag.name(); // RSI:BUY
    }
}
