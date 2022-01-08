package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class StrategyFactory {

    public Strategy create(StrategyCode strategyCode, ExchangeProcessor processor, CoinType coinType, TradingTerm tradingTerm) {
        if (strategyCode == StrategyCode.RSI) {
            return new RsiTradingStrategy(coinType, tradingTerm, processor);
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
