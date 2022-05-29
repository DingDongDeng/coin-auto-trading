package com.dingdongdeng.coinautotrading.trading.exchange.spot.service.selector;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeCandleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpotExchangeCandleServiceSelector {

    private final List<SpotExchangeCandleService> candleServiceList;

    public SpotExchangeCandleService getTargetService(CoinExchangeType coinExchangeType) {
        return candleServiceList.stream()
            .filter(processor -> processor.getCoinExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }
}
