package com.dingdongdeng.coinautotrading.autotrading.service;

import com.dingdongdeng.coinautotrading.autotrading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.autotrading.type.AutoTradingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingService {

    private AutoTradingStatus status = AutoTradingStatus.INIT;

    @Async
    public void execute(Strategy strategy) {
        while (status == AutoTradingStatus.RUNNING) {
            delay(1000);
            strategy.execute();
        }
    }

    public void updateStatus(AutoTradingStatus status) {
        this.status = status;
    }

    public boolean isRunning() {
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
