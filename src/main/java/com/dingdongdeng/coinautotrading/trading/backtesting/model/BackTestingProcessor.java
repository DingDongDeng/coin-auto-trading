package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.backtesting.context.BackTestingContextLoader;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.type.BackTestingProcessStatus;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
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
    private String userId;
    private String autoTradingProcessorId;
    @Default
    private BackTestingProcessStatus status = BackTestingProcessStatus.INIT;
    private Strategy strategy;
    private BackTestingContextLoader backTestingContextLoader;
    private long duration;

    public void start() {
        CompletableFuture.runAsync(this::process);
    }

    private void process() {
        try {
            this.status = BackTestingProcessStatus.RUNNING;
            while (backTestingContextLoader.hasNext()) {
                delay();
                // now를 백테스팅 시점인 과거로 재정의
                TradingTimeContext.nowSupplier(() -> backTestingContextLoader.getCurrentContext().getNow());

                // 백테스팅 사이클 실행
                strategy.execute();
            }
        } catch (Exception e) {
            log.error("backTesting error : ", e); //fixme 여기서 로깅하지 않도록 수정
            this.status = BackTestingProcessStatus.FAILED;
            throw e;
        } finally {
            // 거래 관련 시간 컨텍스트 초기화
            TradingTimeContext.clear();

            if (this.status != BackTestingProcessStatus.FAILED) {
                this.status = BackTestingProcessStatus.COMPLETED;
            }
        }
    }

    private void delay() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
