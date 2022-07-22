package com.dingdongdeng.coinautotrading.trading.autotrading.model;

import com.dingdongdeng.coinautotrading.common.slack.SlackSender;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingProcessStatus;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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

    @Default
    private String id = UUID.randomUUID().toString();
    private String title;
    private String userId;
    private CoinType coinType;
    private CoinExchangeType coinExchangeType;
    private AutoTradingProcessStatus status;
    private TradingTerm tradingTerm;
    private Strategy<?, ?> strategy;
    private long duration;
    private SlackSender slackSender;

    public void start() {
        //fixme 로그 추적을 위해 id, userId를 찍을 수 있어야함
        if (isRunning()) {
            return;
        }
        this.status = AutoTradingProcessStatus.RUNNING;
        CompletableFuture.runAsync(this::process); //fixme AsyncDecorater 설정이 먹힐려나>???
    }

    public void stop() {
        this.status = AutoTradingProcessStatus.STOPPED;
    }

    public void terminate() {
        this.status = AutoTradingProcessStatus.TERMINATED;
    }

    private void process() {
        this.strategy.ready();
        while (isRunning()) {
            log.info("running autoTradingProcessor...");
            delay();
            try {
                this.strategy.execute();
            } catch (Exception e) {
                log.error("strategy execute exception : {}", e.getMessage(), e);
                slackSender.send("userId : " + userId + ", title : " + title + ", autoTradingProcessorId : " + id, e);
            }
        }
    }

    private boolean isRunning() {
        return this.status == AutoTradingProcessStatus.RUNNING;
    }

    private void delay() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
