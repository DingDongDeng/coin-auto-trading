package com.dingdongdeng.coinautotrading.trading.autotrading.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
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
    public CommonResponse<Void> register(@RequestBody AutoTradingRegisterRequest request, @RequestHeader String userId) {
        autoTradingService.register(request, userId);
        return CommonResponse.<Void>builder()
            .message("autotrading register success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/start")
    public CommonResponse<Void> start(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        autoTradingService.start(autoTradingProcessorId, userId);
        return CommonResponse.<Void>builder()
            .message("autotrading start success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/stop")
    public CommonResponse<Void> stop(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        autoTradingService.stop(autoTradingProcessorId, userId);
        return CommonResponse.<Void>builder()
            .message("autotrading stop success")
            .build();
    }

    @PostMapping("/{autoTradingProcessorId}/terminate")
    public CommonResponse terminate(@PathVariable String autoTradingProcessorId, @RequestHeader String userId) {
        autoTradingService.terminate(autoTradingProcessorId, userId);
        return CommonResponse.<Void>builder()
            .message("autotrading terminate success")
            .build();
    }

}
