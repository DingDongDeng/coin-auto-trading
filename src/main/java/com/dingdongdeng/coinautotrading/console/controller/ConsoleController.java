package com.dingdongdeng.coinautotrading.console.controller;

import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import com.dingdongdeng.coinautotrading.common.slack.SlackSender;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.console.controller.model.TypeInfoResponse;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConsoleController {

    @Autowired
    private SlackSender slackSender;

    @RequestMapping("/")
    public String index() {
        slackSender.send(new RuntimeException("발송테스트"));
        return "index";
    }

    @GetMapping("/type")
    @ResponseBody
    public CommonResponse<TypeInfoResponse> getTypeInfo() {
        return CommonResponse.<TypeInfoResponse>builder()
            .body(
                TypeInfoResponse.builder()
                    .coinTypeMap(CoinType.toMap())
                    .coinExchangeTypeMap(CoinExchangeType.toMap())
                    .tradingTermMap(TradingTerm.toMap())
                    .strategyCodeMap(StrategyCode.toMap())
                    .build()
            )
            .message("get type info success")
            .build();
    }

}
