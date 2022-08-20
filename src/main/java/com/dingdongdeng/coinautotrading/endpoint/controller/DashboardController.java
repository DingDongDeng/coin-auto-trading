package com.dingdongdeng.coinautotrading.endpoint.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.endpoint.controller.DashboardController.TypeInfoResponse.TypeInfoTemplate;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/type")
    @ResponseBody
    public CommonResponse<TypeInfoResponse> getTypeInfo() {
        return CommonResponse.<TypeInfoResponse>builder()
            .body(
                TypeInfoResponse.builder()
                    .coinTypeList(TypeInfoTemplate.listOf(CoinType.toMap()))
                    .coinExchangeTypeList(TypeInfoTemplate.listOf(CoinExchangeType.toMap()))
                    .tradingTermList(TypeInfoTemplate.listOf(TradingTerm.toMap()))
                    .strategyCodeList(TypeInfoTemplate.listOf(StrategyCode.toMap()))
                    .build()
            )
            .message("get type info success")
            .build();
    }


    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TypeInfoResponse {

        private List<TypeInfoTemplate> coinTypeList;
        private List<TypeInfoTemplate> coinExchangeTypeList;
        private List<TypeInfoTemplate> tradingTermList;
        private List<TypeInfoTemplate> strategyCodeList;

        @ToString
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class TypeInfoTemplate {

            private String name;
            private Object value;

            public static List<TypeInfoTemplate> listOf(Map<?, String> enumMap) {
                return enumMap.entrySet().stream()
                    .map(entry -> new TypeInfoTemplate(entry.getValue(), entry.getKey()))
                    .collect(Collectors.toList());
            }
        }
    }
}
