package com.dingdongdeng.coinautotrading.admin.service;

import com.dingdongdeng.coinautotrading.admin.type.Command;
import com.dingdongdeng.coinautotrading.autotrading.service.AutoTradingService;
import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AutoTradingService autoTradingService;

    public void command(CoinExchangeType coinExchangeType, Command command, StrategyCode strategyCode) {

        if (command == Command.STOP) {
            autoTradingService.stop();
            return;
        }

        if (command == Command.START) {
            autoTradingService.start(coinExchangeType, strategyCode);
        }
    }
}
