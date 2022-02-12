package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeCandleService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpbitCandleStoreJob extends CandleStoreJob {

    public UpbitCandleStoreJob(ExchangeCandleService upbitExchangeCandleService, CandleService candleService) {
        super(List.of(CoinType.ETHEREUM), CandleUnit.UNIT_1M, upbitExchangeCandleService, candleService);
    }
}
