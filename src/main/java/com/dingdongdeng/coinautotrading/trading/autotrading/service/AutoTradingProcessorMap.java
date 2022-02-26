package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public List<AutoTradingProcessor> getList(String userId) { //fixme map을 이렇게 순회하지 않도록 수정 필요(db에서 읽던가)
        return processorMap.values().stream()
            .filter(processor -> processor.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    public void put(AutoTradingProcessor processor) {
        processorMap.put(processor.getId(), processor);
    }

    public void remove(String processorId) {
        processorMap.remove(processorId);
    }
}
