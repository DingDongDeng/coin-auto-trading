package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.domain.entity.ExchangeCandle;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpbitCandleStoreJob extends CandleStoreJob {

    private final ExchangeService upbitExchangeService;

    public UpbitCandleStoreJob(ExchangeService upbitExchangeService, ExchangeCandleService exchangeCandleService) {
        super(exchangeCandleService);
        this.upbitExchangeService = upbitExchangeService;
    }

    @Override
    protected List<ExchangeCandle> getExchangeCandleList() {
        return Collections.EMPTY_LIST;
    }
}
