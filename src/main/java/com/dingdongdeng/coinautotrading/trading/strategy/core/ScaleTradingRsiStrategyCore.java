package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ScaleTradingRsiStrategyCore implements StrategyCore {

    private final ScaleTradingRsiStrategyCoreParam param;

    /*
     *  RSI 기반 물타기 매매 전략
     *
     *  매수 시점
     *  - RSI가 낮아졌을때
     *  - 손실금액이 일정비율 이상 커질때 마다
     *
     *  익절 시점
     *  - RSI가 높아졌을때
     *  - 목표한 이익금에 도달했을때
     *
     *  손절 시점
     *  - RSI가 높아졌지만 손실 중일때
     */
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
        List<TradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        List<TradingResult> profitTradingResultList = tradingResultPack.getProfitTradingResultList();
        List<TradingResult> lossTradingResultList = tradingResultPack.getLossTradingResultList();

        /*
         * 미체결 상태가 너무 오래되면, 주문을 취소
         */
        for (TradingResult tradingResult : tradingResultPack.getAll()) {
            if (tradingResult.isDone()) {
                continue;
            }
            // 오래된 주문 건이 존재
            if (isTooOld(tradingResult)) {
                log.info("{} :: 미체결 상태의 오래된 주문을 취소", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderId(tradingResult.getOrderId())
                        .orderType(OrderType.CANCEL)
                        .volume(tradingResult.getVolume())
                        .price(tradingResult.getPrice())
                        .priceType(tradingResult.getPriceType())
                        .tag(tradingResult.getTag())
                        .build()
                );
            }
            // 체결이 될때까지 기다리기 위해 아무것도 하지 않음
            log.info("{} :: 미체결 건을 기다림", identifyCode);
            return List.of();
        }

        /*
         * 매수 주문이 체결된 후 현재 가격을 모니터링하다가 익절/손절 주문을 요청함
         */
        if (!buyTradingResultList.isEmpty()) {
            log.info("{} :: 매수 주문이 체결된 상태임", identifyCode);
            double currentPrice = tradingInfo.getCurrentPrice();

            // 익절/손절이 체결되어 보유한 물량이 없음
            if (!tradingResultPack.getProfitTradingResultList().isEmpty()) { //fixme 부동소주점 때문에 조건을 이렇게 바꿈...원래는 getVolume() 0인지 봐야함
                log.info("{} :: 익절, 손절 주문이 체결되고 남은 물량이 없음", identifyCode);
                //매수, 익절, 손절에 대한 정보를 모두 초기화
                return List.of(
                    TradingTask.builder().isReset(true).build()
                );
            }

            //익절 주문
            if (isProfitOrderTiming(currentPrice, rsi, tradingResultPack)) {
                log.info("{} :: 익절 주문 요청", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderType(OrderType.SELL)
                        .volume(tradingResultPack.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.PROFIT)
                        .build()
                );
            }

            //손절 주문
            if (isLossOrderTiming(currentPrice, rsi, tradingResultPack)) {
                log.info("{} :: 부분 또는 전부 손절 주문 요청", identifyCode);
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderType(OrderType.SELL)
                        .volume(tradingResultPack.getBuyVolume() / param.getBuyCountLimit())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.LOSS)
                        .build()
                );
            }
        }

        /*
         * rsi가 조건을 만족하고, 매수주문을 한적이 없다면 매수주문을 함
         */
        if (isBuyOrderTiming(rsi, tradingInfo.getCurrentPrice(), tradingResultPack)) {
            if (!isEnoughBalance(tradingInfo.getBalance())) {
                log.warn("{} :: 계좌가 매수 가능한 상태가 아님", identifyCode);
                return List.of(new TradingTask());
            }

            log.info("{} :: 매수 주문 요청", identifyCode);
            double currentPrice = tradingInfo.getCurrentPrice();
            double volume = buyTradingResultList.isEmpty() ? param.getOrderPrice() / currentPrice : tradingResultPack.getVolume() * param.getBuyVolumeRate();
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

        return List.of();
    }

    @Override
    public void handleOrderResult(TradingResult tradingResult) {

    }

    @Override
    public void handleOrderCancelResult(TradingResult tradingResult) {

    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }

    private boolean isEnoughBalance(double balance) {
        return balance > param.getAccountBalanceLimit();
    }

    private boolean isBuyOrderTiming(double rsi, double currentPrice, TradingResultPack tradingResultPack) {
        List<TradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        List<TradingResult> lossTradingResultList = tradingResultPack.getLossTradingResultList();

        // 추가 매수가 가능한지 확인
        if (param.getBuyCountLimit() <= buyTradingResultList.size() - lossTradingResultList.size()) {
            return false;
        }

        if (rsi < param.getBuyRsi()) {
            if (buyTradingResultList.isEmpty()) {
                return true;
            }

            // 손익율 계산
            double averagePrice = tradingResultPack.getAveragePrice();
            if ((averagePrice - currentPrice) / averagePrice > param.getBuyLossRate()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isProfitOrderTiming(double currentPrice, double rsi, TradingResultPack tradingResultPack) {
        // 이익 중일때, 이익 한도에 다다르면 익절
        if (currentPrice >= tradingResultPack.getAveragePrice() * (1 + param.getProfitLimitPriceRate())) {
            return true;
        }

        // 이익 중일때, RSI 이미 상승했다면 익절
        if (currentPrice > tradingResultPack.getAveragePrice() && rsi >= param.getProfitRsi()) {
            return true;
        }
        return false;
    }

    private boolean isLossOrderTiming(double currentPrice, double rsi, TradingResultPack tradingResultPack) {
        List<TradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        List<TradingResult> lossTradingResultList = tradingResultPack.getLossTradingResultList();

        // 아직 추가 매수가 가능하다면 손절하지 않음
        if (param.getBuyCountLimit() > buyTradingResultList.size() - lossTradingResultList.size()) {
            return false;
        }

        // 손실 중일때, 손실 한도에 다다르면 손절
        if (currentPrice < tradingResultPack.getAveragePrice() * (1 - param.getLossLimitPriceRate())) {
            return true;
        }

        // 손실 중일때, RSI가 이미 상승했다면 손절
        if (currentPrice < tradingResultPack.getAveragePrice() && rsi >= param.getLossRsi()) {
            return true;
        }
        return false;
    }

    private boolean isTooOld(TradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
