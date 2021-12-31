package com.dingdongdeng.coinautotrading.admin.service;

import com.dingdongdeng.coinautotrading.admin.type.Command;
import com.dingdongdeng.coinautotrading.autotrading.service.AutoTradingService;
import com.dingdongdeng.coinautotrading.autotrading.strategy.PrototypeStrategy;
import com.dingdongdeng.coinautotrading.autotrading.strategy.Strategy;
import com.dingdongdeng.coinautotrading.autotrading.type.AutoTradingStatus;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AutoTradingService autoTradingService;
    private final List<ExchangeProcessor> processorList;

    public void command(CoinExchangeType coinExchangeType, Command command) {

        if (command == Command.STOP) {
            autoTradingService.updateStatus(AutoTradingStatus.STOPPED);
            return;
        }

        if (command == Command.START) {
            if (autoTradingService.isRunning()) {
                return;
            }
            autoTradingService.updateStatus(AutoTradingStatus.RUNNING);
            autoTradingService.execute(makeTargetStrategy(getTargetProcessor(coinExchangeType)));
        }
    }

    private ExchangeProcessor getTargetProcessor(CoinExchangeType coinExchangeType) {
        return processorList.stream()
            .filter(processor -> processor.getExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow();
    }

    private Strategy makeTargetStrategy(ExchangeProcessor processor) {
        return new PrototypeStrategy(processor);
    }
}
