package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AutoTradingManager {

  private final AutoTradingProcessorMap processorMap; // fixme 분산환경을 고려해서 구성이 가능한 내용일까???

  public List<AutoTradingProcessor> getList(String userId) {
    return processorMap.getList(userId);
  }

  public AutoTradingProcessor register(AutoTradingProcessor processor) {
    processorMap.put(processor);
    return processor;
  }

  public AutoTradingProcessor start(String processorId, String userId) {
    AutoTradingProcessor processor = get(processorId);
    validate(userId, processor);
    processor.start();
    return processor;
  }

  public AutoTradingProcessor stop(String processorId, String userId) {
    AutoTradingProcessor processor = get(processorId);
    validate(userId, processor);
    processor.stop();
    return processor;
  }

  public AutoTradingProcessor terminate(String processorId, String userId) {
    AutoTradingProcessor processor = get(processorId);
    validate(userId, processor);
    processor.terminate();
    processorMap.remove(processorId);
    return processor;
  }

  private AutoTradingProcessor get(String processorId) {
    return processorMap.get(processorId);
  }

  private void validate(String userId, AutoTradingProcessor processor) {
    if (Objects.isNull(processor)) {
      throw new NoSuchElementException("not found processor");
    }
    if (!processor.getUserId().equals(userId)) {
      throw new RuntimeException("잘못된 접근 입니다. \nuserId : " + userId);
    }
  }
}
