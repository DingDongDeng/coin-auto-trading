package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AutoTradingManager {

    private final AutoTradingProcessorMap processorMap; //fixme 분산환경을 고려해서 구성이 가능한 내용일까???

    public void register(AutoTradingProcessor processor) {
        processorMap.put(processor);
    }

    public void start(String processorId, String userId) {
        AutoTradingProcessor processor = get(processorId);
        validate(userId, processor);
        processor.start();
    }

    public void stop(String processorId, String userId) {
        AutoTradingProcessor processor = get(processorId);
        validate(userId, processor);
        processor.stop();
    }

    public void terminate(String processorId, String userId) {
        AutoTradingProcessor processor = get(processorId);
        validate(userId, processor);
        processor.terminate();
        processorMap.remove(processorId);
    }

    private AutoTradingProcessor get(String processorId) {
        return processorMap.get(processorId);
    }

    private void validate(String userId, AutoTradingProcessor processor) {
        if (Objects.isNull(processor)) {
            throw new NoSuchElementException("not found processor");
        }
        if (!processor.getUserId().equals(userId)) {
            throw new IllegalArgumentException("userId : " + userId);
        }
    }
}
