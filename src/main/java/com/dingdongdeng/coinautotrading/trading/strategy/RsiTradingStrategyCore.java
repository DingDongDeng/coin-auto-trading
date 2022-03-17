package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategyCore implements StrategyCore {

    private final double BUY_RSI = 0.25; // 매수 주문을 할 rsi 기준
    private final double PROFIT_RSI = 0.50; // 이익중일때 익절할 rsi 기준
    private final double LOSS_RSI = 0.40; // 손실중일때 손절할 rsi 기준
    private final double PROFIT_LIMIT_PRICE_RATE = 0.02; // 익절 이익율 상한
    private final double LOSS_LIMIT_PRICE_RATE = 0.01; // 손절 손실율 상한
    private final int TOO_OLD_ORDER_TIME_SECONDS = 30; // 초(second)
    private final double ORDER_PRICE = 10000;
    private final double ACCOUNT_BALANCE_LIMIT = 300 * 10000; //계좌 금액 안전 장치

    @Override
    public List<TradingTask> makeTradingTask(TradingInfo tradingInfo) {
        String identifyCode = tradingInfo.getIdentifyCode();
        log.info("{} :: ---------------------------------------", identifyCode);
        CoinType coinType = tradingInfo.getCoinType();
        TradingTerm tradingTerm = tradingInfo.getTradingTerm();
        double rsi = tradingInfo.getRsi();

        log.info("tradingInfo : {}", tradingInfo);
        log.info("{} :: coinType={}", identifyCode, coinType);
        log.info("{} :: rsi={}", identifyCode, rsi);

        // 자동매매 중 기억해야할 실시간 주문 정보(익절, 손절, 매수 주문 정보)
        TradingResultPack tradingResultPack = tradingInfo.getTradingResultPack();
        TradingResult buyTradingResult = tradingResultPack.getBuyTradingResult();
        TradingResult profitTradingResult = tradingResultPack.getProfitTradingResult();
        TradingResult lossTradingResult = tradingResultPack.getLossTradingResult();

        /**
         * 미체결 상태가 너무 오래되면, 주문을 취소
         */
        for (TradingResult tradingResult : List.of(buyTradingResult, profitTradingResult, lossTradingResult)) {
            if (!tradingResult.isExist()) {
                continue;
            }
            if (!tradingResult.isDone() && isTooOld(tradingResult)) {
                log.info("{} :: 미체결 상태의 오래된 주문을 취소", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderId(tradingResult.getOrderId())
                        .orderType(OrderType.CANCEL)
                        .tag(tradingResult.getTag())
                        .build()
                );
            }
        }

        /**
         * 매수 주문이 체결된 후 현재 가격을 모니터링하다가 익절/손절 주문을 요청함
         */
        if (buyTradingResult.isDone()) {
            log.info("{} :: 매수 주문이 체결된 상태임", identifyCode);
            double currentPrice = tradingInfo.getCurrentPrice();

            // 익절/손절 중복 요청 방지
            if (profitTradingResult.isExist() || lossTradingResult.isExist()) {
                log.info("{} :: 익절, 손절 주문을 했었음", identifyCode);

                // 익절/손절 체결된 경우 정보 초기화
                if (profitTradingResult.isDone() || lossTradingResult.isDone()) {
                    log.info("{} :: 익절, 손절 주문이 체결되었음", identifyCode);
                    //매수, 익절, 손절에 대한 정보를 모두 초기화
                    return List.of(
                        TradingTask.builder().isReset(true).build()
                    );
                }
                return List.of(new TradingTask());
            }

            //익절 주문
            if (isProfitOrderTiming(currentPrice, rsi, buyTradingResult)) {
                log.info("{} :: 익절 주문 요청", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderType(OrderType.SELL)
                        .volume(buyTradingResult.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.PROFIT)
                        .build()
                );
            }

            //손절 주문
            if (isLossOrderTiming(currentPrice, rsi, buyTradingResult)) {
                log.info("{} :: 손절 주문 요청", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
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
        if (isBuyOrderTiming(rsi, buyTradingResult)) {
            if (!isEnoughBalance(tradingInfo.getBalance())) {
                log.warn("{} :: 계좌가 매수 가능한 상태가 아님", identifyCode);
                return List.of(new TradingTask());
            }

            log.info("{} :: 매수 주문 요청", identifyCode);
            double currentPrice = tradingInfo.getCurrentPrice();
            double volume = ORDER_PRICE / currentPrice;
            return List.of(
                TradingTask.builder()
                    .identifyCode(identifyCode)
                    .coinType(coinType)
                    .tradingTerm(tradingTerm)
                    .orderType(OrderType.BUY)
                    .volume(volume)
                    .price(currentPrice)
                    .priceType(PriceType.LIMIT_PRICE)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }

        log.info("{} :: 아무것도 하지 않음", identifyCode);
        return List.of(new TradingTask());
    }

    @Override
    public void handleOrderResult(TradingResult tradingResult) {

    }

    @Override
    public void handleOrderCancelResult(TradingResult tradingResult) {

    }

    private boolean isEnoughBalance(double balance) {
        return balance > ACCOUNT_BALANCE_LIMIT;
    }

    private boolean isBuyOrderTiming(double rsi, TradingResult buyTradingResult) {
        return !buyTradingResult.isExist() && rsi < BUY_RSI;
    }

    private boolean isProfitOrderTiming(double currentPrice, double rsi, TradingResult buyTradingResult) {
        // 이익 중일때, 이익 한도에 다다르면 익절
        if (currentPrice >= buyTradingResult.getPrice() * (1 + PROFIT_LIMIT_PRICE_RATE)) {
            return true;
        }

        // 이익 중일때, RSI 이미 상승했다면 익절
        if (currentPrice > buyTradingResult.getPrice() && rsi >= PROFIT_RSI) {
            return true;
        }
        return false;
    }

    private boolean isLossOrderTiming(double currentPrice, double rsi, TradingResult buyTradingResult) {
        // 손실 중일때, 손실 한도에 다다르면 손절
        if (currentPrice <= buyTradingResult.getPrice() * (1 - LOSS_LIMIT_PRICE_RATE)) {
            return true;
        }

        // 손실 중일때, RSI가 이미 상승했다면 손절
        if (currentPrice <= buyTradingResult.getPrice() && rsi >= LOSS_RSI) {
            return true;
        }
        return false;
    }

    private boolean isTooOld(TradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), LocalDateTime.now()) >= TOO_OLD_ORDER_TIME_SECONDS;
    }
}
