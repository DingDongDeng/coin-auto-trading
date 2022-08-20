package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyExecuteResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Getter //fixme Strategy가 너무 외부에 많이 노출됨
@Slf4j
public class Strategy<TI extends TradingInfo, TR extends TradingResult> {

    private final String identifyCode;
    private final StrategyCode strategyCode;
    private final StrategyCore<TI, TR> strategyCore;
    private final StrategyService<TI, TR> strategyService;
    private final StrategyStore<TR> strategyStore;
    private final StrategyRecorder<TR> strategyRecorder;

    public Strategy(StrategyCode code, StrategyCore<TI, TR> core, StrategyService<TI, TR> service, StrategyStore<TR> store) {
        this.strategyCode = code;
        this.identifyCode = code.name() + "-" + UUID.randomUUID();
        this.strategyCore = core;
        this.strategyService = service;
        this.strategyStore = store;
    }

    public void ready() {
        MDC.put("identifyCode", identifyCode);
        strategyService.ready(strategyCore.getParam());
    }

    public StrategyExecuteResult execute() {
        MDC.put("executeId", UUID.randomUUID().toString());

        // 주문 정보 갱신 및 생성
        TradingResultPack<TR> tradingResultPack = strategyStore.get();
        TradingResultPack<TR> updatedTradingResultPack = strategyService.updateTradingResultPack(tradingResultPack);
        strategyStore.saveAll(updatedTradingResultPack);
        TI tradingInfo = strategyService.getTradingInformation(identifyCode);

        List<TradingTask> tradingTaskList = strategyCore.makeTradingTask(tradingInfo, updatedTradingResultPack);
        log.info("tradingTaskList : {}", tradingTaskList);

        tradingTaskList.forEach(tradingTask -> {
            // 모든 정보 초기화
            if (isReset(tradingTask)) {
                strategyStore.resetAll();
                return;
            }

            // 매수, 매도 주문
            if (isOrder(tradingTask)) {
                TR orderTradingResult = strategyService.order(tradingTask);
                strategyStore.save(orderTradingResult); // 주문 성공 건 정보 저장
                strategyCore.handleOrderResult(orderTradingResult);
                return;
            }

            // 주문 취소
            if (isOrderCancel(tradingTask)) {
                TR cancelTradingResult = strategyService.orderCancel(tradingTask);
                strategyStore.delete(cancelTradingResult); // 주문 취소 건 정보 제거
                strategyCore.handleOrderCancelResult(cancelTradingResult);
                return;
            }

            // 아무것도 하지 않음
            log.info("do nothing");
        });

        return new StrategyExecuteResult(tradingInfo, strategyStore.get());
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
