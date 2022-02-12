package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyFactory {

    private final StrategyAssistant strategyAssistant;

    public Strategy create(StrategyCode strategyCode, ExchangeService exchangeService, CoinType coinType, TradingTerm tradingTerm) {
        if (strategyCode == StrategyCode.RSI) {
            return new RsiTradingStrategy(coinType, tradingTerm, exchangeService, strategyAssistant);
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
