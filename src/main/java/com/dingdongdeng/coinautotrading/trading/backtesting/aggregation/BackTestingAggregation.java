package com.dingdongdeng.coinautotrading.trading.backtesting.aggregation;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingProcessor;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingRequest;
import com.dingdongdeng.coinautotrading.trading.backtesting.service.BackTestingService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingAggregation {


    private final BackTestingService backTestingService;
    private final AutoTradingService autoTradingService;

    public String doTest(BackTestingRequest.Register request, String userId) {
        List<AutoTradingProcessor> autoTradingProcessorList = autoTradingService.getUserProcessorList(userId);
        AutoTradingProcessor autoTradingProcessor = autoTradingProcessorList.stream()
            .filter(p -> p.getId().equals(request.getAutoTradingProcessorId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("백테스팅 하기 위한 자동매매를 찾지 못했습니다."));

        return UUID.randomUUID().toString(); // backTestingId
    }

    public Object getResult(String backTestingId) {
        return null;
    }
}
