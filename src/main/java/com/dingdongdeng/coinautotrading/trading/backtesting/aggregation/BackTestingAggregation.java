package com.dingdongdeng.coinautotrading.trading.backtesting.aggregation;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingProcessor;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingRequest;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingResponse;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingResponse.Result;
import com.dingdongdeng.coinautotrading.trading.backtesting.service.BackTestingService;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyRecorder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingAggregation {

    private final BackTestingService backTestingService;
    private final AutoTradingService autoTradingService;

    public BackTestingResponse doTest(BackTestingRequest.Register request, String userId) {
        List<AutoTradingProcessor> autoTradingProcessorList = autoTradingService.getUserProcessorList(userId);
        AutoTradingProcessor autoTradingProcessor = autoTradingProcessorList.stream()
            .filter(p -> p.getId().equals(request.getAutoTradingProcessorId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("백테스팅 하기 위한 자동매매를 찾지 못했습니다."));

        BackTestingProcessor backTestingProcessor = backTestingService.doTest(autoTradingProcessor, request.getStart(), request.getEnd());

        return BackTestingResponse.builder()
            .backTestingId(backTestingProcessor.getId())
            .userId(backTestingProcessor.getUserId())
            .autoTradingProcessorId(backTestingProcessor.getAutoTradingProcessorId())
            .start(backTestingProcessor.getStart())
            .end(backTestingProcessor.getEnd())
            .result(null)
            .build();
    }

    public List<BackTestingResponse> getResult(String userId) {
        return backTestingService.getBackTestingProcessorList(userId).stream()
            .map(b -> {
                StrategyRecorder recorder = b.getStrategy().getStrategyRecorder();
                LocalDateTime start = b.getStart();
                LocalDateTime end = b.getEnd();
                double totalTime = ChronoUnit.MINUTES.between(start, end);
                double executionTime = ChronoUnit.MINUTES.between(start, Optional.ofNullable(b.getNow()).orElse(start));

                return BackTestingResponse.builder()
                    .backTestingId(b.getId())
                    .userId(b.getUserId())
                    .autoTradingProcessorId(b.getAutoTradingProcessorId())
                    .start(start)
                    .end(end)
                    .result(
                        Result.builder()
                            .status(b.getStatus())
                            .executionRate(executionTime / totalTime)
                            .marginPrice(recorder.getMarginPrice())
                            .marginRate(recorder.getMarginRate())
                            .eventMessage(recorder.getEventMessage())
                            .build()
                    )
                    .build();
            })
            .collect(Collectors.toList());
    }
}
