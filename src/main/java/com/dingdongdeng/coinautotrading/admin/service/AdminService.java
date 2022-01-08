package com.dingdongdeng.coinautotrading.admin.service;

import com.dingdongdeng.coinautotrading.admin.model.CommandRequest;
import com.dingdongdeng.coinautotrading.admin.model.type.Command;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingStartParam;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AutoTradingService autoTradingService;

    public void command(Command command, CommandRequest request) {

        if (command == Command.STOP) {
            autoTradingService.stop();
            return;
        }

        if (command == Command.START) {
            autoTradingService.start(makeAutoTradingStartParam(request));
        }
    }

    private AutoTradingStartParam makeAutoTradingStartParam(CommandRequest request) {
        return AutoTradingStartParam.builder()
            .coinType(request.getCoinType())
            .coinExchangeType(request.getCoinExchangeType())
            .tradingTerm(request.getTradingTerm())
            .strategyCode(request.getStrategyCode())
            .build();
    }
}
