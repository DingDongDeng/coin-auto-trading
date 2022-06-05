package com.dingdongdeng.coinautotrading.trading.strategy.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.trading.strategy.annotation.GuideMessage;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyMetaResponse;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyMetaResponse.ParamMeta;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
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
    public CommonResponse<StrategyMetaResponse> getMeta(@PathVariable String strategyCode) {
        StrategyCode code = StrategyCode.of(strategyCode.toUpperCase());
        Field[] fields = code.getStrategyCoreParamClazz().getDeclaredFields();
        return CommonResponse.<StrategyMetaResponse>builder()
            .body(
                StrategyMetaResponse.builder()
                    .strategyCode(code)
                    .marketType(code.getMarketType())
                    .paramMetaList(
                        Arrays.stream(fields)
                            .map(
                                field -> ParamMeta.builder()
                                    .name(field.getName())
                                    .guideMessage(field.getAnnotation(GuideMessage.class).value())
                                    .type(field.getType().getSimpleName())
                                    .build()
                            ).collect(Collectors.toList())

                    )
                    .build()
            )
            .message("success")
            .build();
    }

}
