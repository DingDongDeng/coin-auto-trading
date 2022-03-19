package com.dingdongdeng.coinautotrading.trading.backtesting.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.backtesting.aggregation.BackTestingAggregation;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingRequest;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BackTestingController {

    private final BackTestingAggregation backTestingAggregation;

    @PostMapping("/backtesting")
    public CommonResponse<BackTestingResponse> doTest(@RequestBody BackTestingRequest.Register request, @RequestHeader String userId) {
        return CommonResponse.<BackTestingResponse>builder()
            .body(backTestingAggregation.doTest(request, userId))
            .message("backtesting doTest success")
            .build();
    }

    @GetMapping("/backtesting/{backTestingId}")
    public CommonResponse<Object> getResult(@PathVariable String backTestingId) {
        return CommonResponse.builder()
            .body(backTestingAggregation.getResult(backTestingId))
            .message("backtesting getResult success")
            .build();
    }
}
