package com.dingdongdeng.coinautotrading.trading.exchange.spot.scheduler;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.impl.UpbitSpotExchangeCandleService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpbitCandleStoreJob extends CandleStoreJob {

    public UpbitCandleStoreJob(UpbitSpotExchangeCandleService upbitSpotExchangeCandleService, CandleService candleService) {
        super(List.of(CoinType.ETHEREUM), CandleUnit.UNIT_1M, upbitSpotExchangeCandleService, candleService);
    }
}
