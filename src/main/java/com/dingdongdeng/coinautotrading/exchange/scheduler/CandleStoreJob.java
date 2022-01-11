package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.domain.entity.ExchangeCandle;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeCandleService;
import java.util.List;
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
        exchangeCandleService.saveAll(getExchangeCandleList());
    }

    abstract List<ExchangeCandle> getExchangeCandleList();
}
