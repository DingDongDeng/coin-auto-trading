package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
@Slf4j
public abstract class CandleStoreJob implements Job {

    private final CandleService candleService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("run CandleStorJob");
        candleService.saveAll(getExchangeCandleList());
    }

    abstract List<Candle> getExchangeCandleList();
}
