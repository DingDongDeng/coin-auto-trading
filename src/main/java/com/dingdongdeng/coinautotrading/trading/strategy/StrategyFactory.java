package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StrategyFactory {

    private final TradingResultRepository tradingResultRepository;

    public Strategy create(StrategyCode strategyCode, ExchangeService exchangeService, CoinType coinType, TradingTerm tradingTerm, String keyPairId) {
        StrategyService strategyService = new StrategyService(coinType, tradingTerm, keyPairId, exchangeService);
        StrategyStore strategyStore = new StrategyStore(tradingResultRepository);
        StrategyCore strategyCore = createStrategyCore(strategyCode);
        return new Strategy(strategyCode, strategyCore, strategyService, strategyStore);
    }

    private StrategyCore createStrategyCore(StrategyCode strategyCode) {
        if (strategyCode == StrategyCode.RSI) {
            return new RsiStrategyCore();
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
