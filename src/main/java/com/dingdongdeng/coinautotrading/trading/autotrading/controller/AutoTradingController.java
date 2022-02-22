package com.dingdongdeng.coinautotrading.trading.autotrading.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/trading/autotrading")
public class AutoTradingController {

    private final AutoTradingService autoTradingService;

    @PostMapping("/register")
    public CommonResponse<Void> register(@RequestBody AutoTradingRegisterRequest request) {
        autoTradingService.register(request);
        return CommonResponse.<Void>builder()
            .message("autotrading register success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/start")
    public CommonResponse<Void> start(@PathVariable String autoTradingProcessorId) {
        autoTradingService.start(autoTradingProcessorId);
        return CommonResponse.<Void>builder()
            .message("autotrading start success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/stop")
    public CommonResponse<Void> stop(@PathVariable String autoTradingProcessorId) {
        autoTradingService.stop(autoTradingProcessorId);
        return CommonResponse.<Void>builder()
            .message("autotrading stop success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/terminate")
    public CommonResponse terminate(@PathVariable String autoTradingProcessorId) {
        autoTradingService.terminate(autoTradingProcessorId);
        return CommonResponse.<Void>builder()
            .message("autotrading terminate success")
            .build();
    }

}
