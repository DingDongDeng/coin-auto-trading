package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyFactory {

    private final TradingResultRepository tradingResultRepository;

    public Strategy create(StrategyCode strategyCode, ExchangeService exchangeService, CoinType coinType, TradingTerm tradingTerm) {
        StrategyAssistant assistant = new StrategyAssistant(exchangeService, tradingResultRepository);
        if (strategyCode == StrategyCode.RSI) {
            return new RsiTradingStrategy(coinType, tradingTerm, exchangeService, assistant); //fixme assistant가 있는데 exchangeService가 있는게 부자연스러움
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
