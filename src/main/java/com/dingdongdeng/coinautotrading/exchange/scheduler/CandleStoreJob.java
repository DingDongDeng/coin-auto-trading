package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.exchange.service.ExchangeCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
@Slf4j
public abstract class CandleStoreJob implements Job {

    private final ExchangeCandleService exchangeCandleService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("run CandleStorJob");
        //fixme CompletedFuture를 활용할수 있는 구조로 해보자
        // transaction 단위는 coinType단위로 끝내서 너무 오랫동안 트랜잭션 잡고있지 않게하자
        // 중복 저장의 위험은 unique 컬럼을 설정해서 방지하는게 좋을거같아(거의 발생안할 이슈라고 판단됨)
    }
}
