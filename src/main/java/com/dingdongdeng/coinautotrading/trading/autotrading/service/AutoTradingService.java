package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeServiceSelector;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingStartParam;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingStatus;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingService {

    private AutoTradingStatus status = AutoTradingStatus.INIT;
    private final ExchangeServiceSelector processorSelector;
    private final StrategyFactory strategyFactory;

    @Async
    public void start(AutoTradingStartParam param) {
        if (isRunning()) {
            return;
        }

        updateStatus(AutoTradingStatus.RUNNING);
        ExchangeService exchangeService = processorSelector.getTargetProcessor(param.getCoinExchangeType());
        Strategy strategy = strategyFactory.create(param.getStrategyCode(), exchangeService, param.getCoinType(), param.getTradingTerm());

        while (isRunning()) {
            log.info("------ beginning of autotrading cycle -----");
            delay(1000);
            try {
                strategy.execute();
            } catch (Exception e) {
                log.error("strategy execute exception : {}", e.getMessage(), e); //fixme 슬랙(이메일은 에러 많이났을때 난사해서 문제될수도)
            }
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
