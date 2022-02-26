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
        StrategyAssistant assistant = new StrategyAssistant(keyPairId, exchangeService, tradingResultRepository);
        if (strategyCode == StrategyCode.RSI) { //fixme exchangeService랑 assistant를 같이 받는게 비효율적임
            return RsiTradingStrategy.builder()
                .coinType(coinType)
                .tradingTerm(tradingTerm)
                .keyPairId(keyPairId)
                .processor(exchangeService)
                .assistant(assistant)
                .build();
        }

        throw new NoSuchElementException("not found strategy code : " + strategyCode);
    }
}
