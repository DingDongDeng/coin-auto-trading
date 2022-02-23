package com.dingdongdeng.coinautotrading.trading.autotrading.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/trading/autotrading")
public class AutoTradingController {

    private final AutoTradingService autoTradingService;

    @PostMapping("/register")
    public CommonResponse<AutoTradingResponse> register(@RequestBody AutoTradingRegisterRequest request, @RequestHeader String keyPairId, @RequestHeader String userId) {
        return CommonResponse.<AutoTradingResponse>builder()
            .body(autoTradingService.register(request, userId))
            .message("autotrading register success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/start")
    public CommonResponse<AutoTradingResponse> start(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        return CommonResponse.<AutoTradingResponse>builder()
            .body(autoTradingService.start(autoTradingProcessorId, userId))
            .message("autotrading start success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/stop")
    public CommonResponse<AutoTradingResponse> stop(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        return CommonResponse.<AutoTradingResponse>builder()
            .body(autoTradingService.stop(autoTradingProcessorId, userId))
            .message("autotrading stop success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/terminate")
    public CommonResponse<AutoTradingResponse> terminate(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        return CommonResponse.<AutoTradingResponse>builder()
            .body(autoTradingService.terminate(autoTradingProcessorId, userId))
            .message("autotrading terminate success")
            .build();
    }

}
