package com.dingdongdeng.coinautotrading.trading.backtesting.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingRequest;
import com.dingdongdeng.coinautotrading.trading.backtesting.service.BackTestingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BackTestingController {

    private final BackTestingService backTestingService;

    @PostMapping("/backtesting")
    public CommonResponse<String> register(@RequestBody BackTestingRequest.Register request) {
        return CommonResponse.<String>builder()
            .body(backTestingService.doTest(request))
            .message("backtesting doTest success")
            .build();
    }

    @GetMapping("/backtesting/{backTestingId}")
    public CommonResponse<Object> getResult(@PathVariable String backTestingId) {
        return CommonResponse.builder()
            .body(backTestingService.getResult(backTestingId))
            .message("backtesting getResult success")
            .build();
    }
}
