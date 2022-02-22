package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AutoTradingProcessorMap {

    private final Map<String, AutoTradingProcessor> processorMap = new HashMap<>(); //fixme 분산환경을 고려해서 구성이 가능한 내용일까???


    public AutoTradingProcessor get(String processorId) {
        return processorMap.get(processorId);
    }

    public void put(AutoTradingProcessor processor) {
        processorMap.put(processor.getId(), processor);
    }

    public void remove(String processorId) {
        processorMap.remove(processorId);
    }
}
