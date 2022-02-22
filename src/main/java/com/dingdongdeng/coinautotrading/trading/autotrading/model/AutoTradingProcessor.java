package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingStatus;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoTradingProcessor {

    private String id;
    private String userId;
    private AutoTradingStatus status;
    private Strategy strategy;
    private long duration;

    public void start() {
        //fixme 로그 추적을 위해 id, userId를 찍을 수 있어야함
        if (isRunning()) {
            return;
        }
        this.status = AutoTradingStatus.RUNNING;
        CompletableFuture.runAsync(this::process); //fixme AsyncDecorater 설정이 먹힐려나>???
    }

    public void stop() {
        this.status = AutoTradingStatus.STOPPED;
    }

    public void terminate() {
        this.status = AutoTradingStatus.TERMINATED;
    }

    private void process() {
        while (isAvail()) {
            log.info("\n------------------------------ beginning of autotrading cycle -----------------------------------------");
            while (isStoped()) {
                log.info("stop");
                delay();
            }
            delay();
            try {
                this.strategy.execute();
            } catch (Exception e) {
                log.error("strategy execute exception : {}", e.getMessage(), e); //fixme 슬랙(이메일은 에러 많이났을때 난사해서 문제될수도)
            }
            log.info("\n------------------------------ end of autotrading cycle -----------------------------------------");
        }
    }

    private boolean isAvail() {
        return this.status == AutoTradingStatus.RUNNING || this.status == AutoTradingStatus.STOPPED;
    }

    private boolean isRunning() {
        return this.status == AutoTradingStatus.RUNNING;
    }

    private boolean isStoped() {
        return this.status == AutoTradingStatus.STOPPED;
    }

    private void delay() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
