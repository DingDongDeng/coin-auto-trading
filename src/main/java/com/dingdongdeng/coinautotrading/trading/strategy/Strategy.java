package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Strategy {

    private final String identifyCode;
    private final StrategyCore strategyCore;
    private final StrategyService strategyService;
    private final StrategyOrderInfoStore strategyOrderInfoStore;

    public Strategy(StrategyCode code, StrategyCore core, StrategyService service, StrategyOrderInfoStore orderInfoStore) {
        this.identifyCode = code.name() + UUID.randomUUID().toString();
        this.strategyCore = core;
        this.strategyService = service;
        this.strategyOrderInfoStore = orderInfoStore;
    }

    public void execute() {
        ExchangeTradingInfo exchangeTradingInfo = strategyService.getTradingInformation(identifyCode);
        List<TradingTask> tradingTaskList = strategyCore.makeTradingTask(exchangeTradingInfo);
        log.info("tradingTaskList : {}", tradingTaskList);

        tradingTaskList.forEach(tradingTask -> {
            // 모든 정보 초기화
            if (isReset(tradingTask)) {
                strategyOrderInfoStore.reset(identifyCode);
                return;
            }

            // 매수, 매도 주문
            if (isOrder(tradingTask)) {
                TradingResult orderTradingResult = strategyService.order(tradingTask);
                strategyOrderInfoStore.storeTradingResult(orderTradingResult); // 주문 성공 건 정보 저장
                strategyCore.handleOrderResult(orderTradingResult);
                return;
            }

            // 주문 취소
            if (isOrderCancel(tradingTask)) {
                TradingResult cancelTradingResult = strategyService.orderCancel(tradingTask);
                strategyOrderInfoStore.reset(cancelTradingResult); // 주문 취소 건 정보 제거
                strategyCore.handleOrderCancelResult(cancelTradingResult);
                return;
            }

            // 아무것도 하지 않음
            log.info("do nothing");
        });
    }

    private boolean isReset(TradingTask tradingTask) {
        return tradingTask.isReset();
    }

    private boolean isOrder(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.BUY || orderType == OrderType.SELL;
    }

    private boolean isOrderCancel(TradingTask tradingTask) {
        OrderType orderType = tradingTask.getOrderType();
        return orderType == OrderType.CANCEL;
    }

}
