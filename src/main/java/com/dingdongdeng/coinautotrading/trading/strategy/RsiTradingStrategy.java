package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderCancel;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    private final double STANDARD_OF_LOW_RSI = 0.30;
    private final double STANDARD_OF_PROFIT_RATE = 1.02;
    private final double STANDARD_OF_LOSS_RATE = 0.985;
    private final int STANDRD_OF_TOO_OLD_TIME = 1; //분(minuite)
    private final double ORDER_PRICE = 5100;
    private final double ACCOUNT_BALANCE_LIMIT = 3000000; //계좌 금액 안전 장치

    private final StrategyAssistant assistant;

    @Builder
    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, String keyPairId, ExchangeService processor, StrategyAssistant assistant) {
        super(coinType, tradingTerm, keyPairId, processor);
        this.assistant = assistant;
    }

    @Override
    protected List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo) {
        log.info("{} :: ---------------------------------------", getCode());
        log.info("tradingInfo : {}", tradingInfo);
        CoinType coinType = tradingInfo.getCoinType();
        double rsi = tradingInfo.getRsi();

        // 자동매매 중 기억해야할 실시간 주문 정보(익절, 손절, 매수 주문 정보)
        TradingResultPack tradingResultPack = assistant.syncedTradingResultPack(getCode());
        TradingResult buyTradingResult = tradingResultPack.getBuyTradingResult();
        TradingResult profitTradingResult = tradingResultPack.getProfitTradingResult();
        TradingResult lossTradingResult = tradingResultPack.getLossTradingResult();

        /**
         * 계좌 상태가 거래 가능한지 확인
         */
        if (!isEnoughBalance(tradingInfo.getBalance())) {
            log.warn("{} :: 계좌가 거래 가능한 상태가 아님", getCode());
            return List.of(new TradingTask());
        }

        /**
         * 미체결 상태가 너무 오래되면, 주문을 취소
         */
        //fixme 리팩토링 필요
        if (!buyTradingResult.isDone() && isTooOld(buyTradingResult)) {
            log.info("{} :: 미체결 상태의 오래된 주문을 취소", getCode());
            return List.of(
                TradingTask.builder()
                    .strategyCode(getCode())
                    .coinType(coinType)
                    .orderId(buyTradingResult.getOrderId())
                    .orderType(OrderType.CANCEL)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }
        if (!profitTradingResult.isDone() && isTooOld(profitTradingResult)) {
            return List.of(
                TradingTask.builder()
                    .strategyCode(getCode())
                    .coinType(coinType)
                    .orderId(profitTradingResult.getOrderId())
                    .orderType(OrderType.CANCEL)
                    .tag(TradingTag.PROFIT)
                    .build()
            );
        }
        if (!lossTradingResult.isDone() && isTooOld(lossTradingResult)) {
            return List.of(
                TradingTask.builder()
                    .strategyCode(getCode())
                    .coinType(coinType)
                    .orderId(lossTradingResult.getOrderId())
                    .orderType(OrderType.CANCEL)
                    .tag(TradingTag.LOSS)
                    .build()
            );
        }

        /**
         * 매수 주문이 체결된 후 현재 가격을 모니터링하다가 익절/손절 주문을 요청함
         */
        if (buyTradingResult.isDone()) {
            log.info("{} :: 매수 주문이 체결된 상태임", getCode());
            double currentPrice = tradingInfo.getTicker().getTradePrice();

            // 익절/손절 중복 요청 방지
            if (profitTradingResult.isExist() || lossTradingResult.isExist()) {
                log.info("{} :: 익절, 손절 주문을 했었음", getCode());

                // 익절/손절 체결된 경우 정보 초기화
                if (profitTradingResult.isDone() || lossTradingResult.isDone()) {
                    log.info("{} :: 익절, 손절 주문이 체결되었음", getCode());
                    //매수, 익절, 손절에 대한 정보를 모두 초기화
                    assistant.reset(getCode());
                }
                return List.of(new TradingTask());
            }

            //익절 주문
            if (currentPrice >= buyTradingResult.getPrice() * STANDARD_OF_PROFIT_RATE) {
                log.info("{} :: 익절 주문 요청", getCode());
                return List.of(
                    TradingTask.builder()
                        .strategyCode(getCode())
                        .coinType(coinType)
                        .orderType(OrderType.SELL)
                        .volume(buyTradingResult.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.PROFIT)
                        .build()
                );
            }

            //손절 주문
            if (currentPrice <= buyTradingResult.getPrice() * STANDARD_OF_LOSS_RATE) {
                log.info("{} :: 손절 주문 요청", getCode());
                return List.of(
                    TradingTask.builder()
                        .strategyCode(getCode())
                        .coinType(coinType)
                        .orderType(OrderType.SELL)
                        .volume(buyTradingResult.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.LOSS)
                        .build()
                );
            }

            return List.of(new TradingTask());
        }

        /**
         * rsi가 조건을 만족하고, 매수주문을 한적이 없다면 매수주문을 함
         */
        if (isBuyTiming(rsi, buyTradingResult)) {
            log.info("{} :: 매수 주문 요청", getCode());
            double price = tradingInfo.getTicker().getTradePrice();
            double volume = ORDER_PRICE / price;

            return List.of(
                TradingTask.builder()
                    .strategyCode(getCode())
                    .coinType(coinType)
                    .orderType(OrderType.BUY)
                    .volume(volume)
                    .price(price)
                    .priceType(PriceType.LIMIT_PRICE)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }

        log.info("{} :: 아무것도 하지 않음", getCode());
        return List.of(new TradingTask());
    }

    @Override
    protected void handleOrderResult(ExchangeOrder order, TradingResult tradingResult) {
        assistant.storeTradingResult(tradingResult); // 주문 성공 건 정보 저장
    }

    @Override
    protected void handleOrderCancelResult(ExchangeOrderCancel orderCancel, TradingResult tradingResult) {
        assistant.reset(tradingResult); // 주문 취소 건 정보 제거
    }

    @Override
    public StrategyCode getCode() {
        return StrategyCode.RSI;
    }

    private boolean isEnoughBalance(double balance) {
        return balance > ACCOUNT_BALANCE_LIMIT;
    }

    private boolean isBuyTiming(double rsi, TradingResult buyTradingResult) {
        return !buyTradingResult.isExist() && rsi < STANDARD_OF_LOW_RSI;
    }

    private boolean isTooOld(TradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.MINUTES.between(tradingResult.getCreatedAt(), LocalDateTime.now()) >= STANDRD_OF_TOO_OLD_TIME;
    }


}
