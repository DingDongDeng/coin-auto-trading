package com.dingdongdeng.coinautotrading.trading.exchange.spot.service.selector;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.SpotExchangeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpotExchangeServiceSelector {

    private final List<SpotExchangeService> spotExchangeServiceList;

    public SpotExchangeService getTargetService(CoinExchangeType coinExchangeType) {
        return spotExchangeServiceList.stream()
            .filter(processor -> processor.getCoinExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }
}
