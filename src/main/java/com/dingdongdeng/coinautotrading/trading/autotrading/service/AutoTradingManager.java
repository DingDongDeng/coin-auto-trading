package com.dingdongdeng.coinautotrading.trading.autotrading.service;

import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingStartParam;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.CommandRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoTradingManager {

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
