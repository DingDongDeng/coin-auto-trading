package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
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

    private final ExchangeService exchangeService;
    private final TradingResultRepository tradingResultRepository;

    public void storeTradingResult(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getStrategyCode(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    public TradingResultPack syncedTradingResultPack(StrategyCode code) {
        //fixme 단건 조회 api로 읽어와서 값 만들고 redis에 업데이트해야함
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

    public void reset(TradingResult tradingResult) {
        StrategyCode code = tradingResult.getStrategyCode();
        TradingTag tag = tradingResult.getTag();
        TradingResult storedTradingResult = findTradingResult(code, tag);
        tradingResultRepository.delete(storedTradingResult);
    }

    private TradingResult findTradingResult(StrategyCode code, TradingTag tag) {
        return tradingResultRepository.findById(getKey(code, tag)).orElse(new TradingResult());
    }

    private String getKey(StrategyCode code, TradingTag tag) {
        return code.name() + ":" + tag.name(); // RSI:BUY
    }
}
