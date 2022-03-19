package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.backtesting.service.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.strategy.Strategy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@AllArgsConstructor
@Builder
public class BackTestingProcessor {

    @Default
    private String id = UUID.randomUUID().toString();
    private Strategy backTestingStrategy;
    private BackTestingContextLoader contextLoader;

    public void start() {
        CompletableFuture.runAsync(this::process);
    }

    private void process() {
        while (contextLoader.hasNext()) {
            backTestingStrategy.execute();
        }
    }
}
