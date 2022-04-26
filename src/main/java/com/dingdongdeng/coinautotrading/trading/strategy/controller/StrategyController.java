package com.dingdongdeng.coinautotrading.trading.strategy.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyMetaResponse;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StrategyController {

    @GetMapping("/{strategyCode}/meta")
    public CommonResponse<StrategyMetaResponse> getMeta(@PathVariable StrategyCode code) {
        return CommonResponse.<StrategyMetaResponse>builder()
            .body(
                StrategyMetaResponse.builder()
                    .strategyCode(code)
                    .paramMetaList(
                        List.of(

                        )
                    )
                    .build()
            )
            .message("success")
            .build();
    }

}
