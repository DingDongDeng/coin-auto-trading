package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeServiceSelector {

    private final List<ExchangeService> processorList;

    public ExchangeService getTargetProcessor(CoinExchangeType coinExchangeType) {
        return processorList.stream()
            .filter(processor -> processor.getExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }
}
