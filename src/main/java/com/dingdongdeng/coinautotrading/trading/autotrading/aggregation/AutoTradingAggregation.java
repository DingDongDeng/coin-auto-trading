package com.dingdongdeng.coinautotrading.trading.autotrading.aggregation;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingAggregation {

    private final AutoTradingService autoTradingService;

    public List<AutoTradingResponse> getUserProcessorList(String userId) {
        return autoTradingService.getUserProcessorList(userId).stream()
            .map(this::makeResponse)
            .collect(Collectors.toList());
    }

    public AutoTradingResponse register(AutoTradingRegisterRequest request, String userId) {
        return makeResponse(autoTradingService.register(request, userId));
    }

    public AutoTradingResponse start(String processorId, String userId) {
        return makeResponse(autoTradingService.start(processorId, userId));
    }

    public AutoTradingResponse stop(String processorId, String userId) {
        return makeResponse(autoTradingService.stop(processorId, userId));
    }

    public AutoTradingResponse terminate(String processorId, String userId) {
        return makeResponse(autoTradingService.terminate(processorId, userId));
    }

    private AutoTradingResponse makeResponse(AutoTradingProcessor processor) {
        return AutoTradingResponse.builder()
            .title(processor.getTitle())
            .processorId(processor.getId())
            .processDuration(processor.getDuration())
            .processStatus(processor.getStatus())
            .userId(processor.getUserId())
            .strategyIdentifyCode(processor.getStrategy().getIdentifyCode())
            .coinType(processor.getCoinType())
            .coinExchangeType(processor.getCoinExchangeType())
            .build();
    }
}
