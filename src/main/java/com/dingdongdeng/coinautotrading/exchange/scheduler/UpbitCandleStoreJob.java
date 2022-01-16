package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.exchange.service.ExchangeCandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpbitCandleStoreJob extends CandleStoreJob {

    public UpbitCandleStoreJob(ExchangeCandleService upbitExchangeCandleService) {
        super(upbitExchangeCandleService);
    }
}
