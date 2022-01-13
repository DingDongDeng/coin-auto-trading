package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpbitCandleStoreJob extends CandleStoreJob {

    private final UpbitClient upbitClient;

    public UpbitCandleStoreJob(UpbitClient upbitClient, CandleService candleService) {
        super(candleService);
        this.upbitClient = upbitClient;
    }

    @Override
    protected List<Candle> getExchangeCandleList() {
        return Collections.EMPTY_LIST;
    }
}
