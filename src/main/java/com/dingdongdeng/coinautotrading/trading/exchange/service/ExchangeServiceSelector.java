package com.dingdongdeng.coinautotrading.trading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeServiceSelector {

    private final List<ExchangeService> processorList;

    public ExchangeService getTargetService(CoinExchangeType coinExchangeType) {
        return processorList.stream()
            .filter(processor -> processor.getCoinExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }
}
