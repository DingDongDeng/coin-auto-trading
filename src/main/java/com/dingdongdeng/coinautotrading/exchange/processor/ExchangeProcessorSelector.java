package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeProcessorSelector {

    private final List<ExchangeProcessor> processorList;

    public ExchangeProcessor getTargetProcessor(CoinExchangeType coinExchangeType) {
        return processorList.stream()
            .filter(processor -> processor.getExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }
}
