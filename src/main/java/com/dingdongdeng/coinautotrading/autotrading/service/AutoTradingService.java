package com.dingdongdeng.coinautotrading.autotrading.service;

import com.dingdongdeng.coinautotrading.autotrading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.autotrading.strategy.StrategyFactory;
import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.autotrading.type.AutoTradingStatus;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessorSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingService {

    private AutoTradingStatus status = AutoTradingStatus.INIT;
    private final ExchangeProcessorSelector processorSelector;
    private final StrategyFactory strategyFactory;

    @Async
    public void start(CoinExchangeType coinExchangeType, StrategyCode strategyCode) {
        if (isRunning()) {
            return;
        }

        updateStatus(AutoTradingStatus.RUNNING);
        ExchangeProcessor processor = processorSelector.getTargetProcessor(coinExchangeType);
        Strategy strategy = strategyFactory.create(strategyCode, processor);

        while (isRunning()) {
            delay(1000);
            strategy.execute();
        }
    }

    public void stop() {
        updateStatus(AutoTradingStatus.STOPPED);
    }

    private void updateStatus(AutoTradingStatus status) {
        this.status = status;
    }

    private boolean isRunning() {
        return this.status == AutoTradingStatus.RUNNING;
    }

    private void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}
