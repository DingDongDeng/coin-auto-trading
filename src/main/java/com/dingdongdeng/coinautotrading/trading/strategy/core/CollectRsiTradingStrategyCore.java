package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.index.Index;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CollectRsiTradingStrategyCore implements StrategyCore<SpotTradingInfo, SpotTradingResult> {

    private final CollectRsiTradingStrategyCoreParam param;
    private LocalDateTime positionCompletedDateTime;

    @Override
    public List<TradingTask> makeTradingTask(SpotTradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack) {
        String identifyCode = tradingInfo.getIdentifyCode();
        CoinType coinType = tradingInfo.getCoinType();
        TradingTerm tradingTerm = tradingInfo.getTradingTerm();
        Index index = tradingInfo.getIndex();

        // 자동매매 중 기억해야할 실시간 주문 정보(익절, 손절, 매수 주문 정보)
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        List<SpotTradingResult> profitTradingResultList = tradingResultPack.getProfitTradingResultList();
        List<SpotTradingResult> lossTradingResultList = tradingResultPack.getLossTradingResultList();

        /*
         * 미체결 상태가 너무 오래되면, 주문을 취소
         */
        for (SpotTradingResult tradingResult : tradingResultPack.getAll()) {
            if (tradingResult.isDone()) {
                continue;
            }
            // 오래된 주문 건이 존재
            if (isTooOld(tradingResult)) {
                log.info(":: 미체결 상태의 오래된 주문을 취소");
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
                        .tag(tradingResult.getTradingTag())
                        .build()
                );
            }
            // 체결이 될때까지 기다리기 위해 아무것도 하지 않음
            log.info(":: 미체결 건을 기다림");
            return List.of();
        }

        //매수, 익절, 손절에 대한 정보를 모두 초기화
        if (!buyTradingResultList.isEmpty()) {
            return List.of(
                TradingTask.builder().isReset(true).build()
            );
        }

        /*
         * 조건을 만족하면 매수 주문
         */
        if (isBuyOrderTiming(tradingInfo.getCurrentPrice(), tradingInfo, tradingResultPack, index)) {

            double currentPrice = tradingInfo.getCurrentPrice();
            double volume = getVolumeForBuy(currentPrice, tradingResultPack);

            if (!isEnoughBalance(tradingInfo.getCurrentPrice(), volume, tradingInfo.getBalance())) {
                log.warn(":: 계좌가 매수 가능한 상태가 아님");
                return List.of(new TradingTask());
            }

            log.info(":: 매수 주문 요청");
            return List.of(
                TradingTask.builder()
                    .identifyCode(identifyCode)
                    .coinType(coinType)
                    .tradingTerm(tradingTerm)
                    .orderType(OrderType.BUY)
                    .volume(volume)
                    .price(currentPrice)
                    .priceType(PriceType.LIMIT)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }

        return List.of();
    }

    @Override
    public void handleOrderResult(SpotTradingInfo tradingInfo, SpotTradingResult tradingResult) {
        if (tradingResult.getTradingTag() == TradingTag.BUY) {
            this.positionCompletedDateTime = tradingResult.getCreatedAt();
        }
    }

    @Override
    public void handleOrderCancelResult(SpotTradingInfo tradingInfo, SpotTradingResult tradingResult) {
        if (tradingResult.getTradingTag() == TradingTag.BUY) {
            this.positionCompletedDateTime = null;
        }
    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }

    private boolean isEnoughBalance(double currentPrice, double buyOrderVolume, double balance) {
        if (balance <= param.getAccountBalanceLimit()) {
            return false;
        }

        if (buyOrderVolume * currentPrice > balance) {
            return false;
        }

        return true;
    }

    private boolean isBuyOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {

        // 가까운 시간에 이미 매수한적이 있다면
        if (!isEnoughBufferTime(this.positionCompletedDateTime)) {
            log.info("[매수 조건] 가까운 시간에 이미 매수한적이 있음, positionCompletedDateTime={}", this.positionCompletedDateTime);
            return false;
        }
        // RSI가 높으면
        if (index.getRsi() > param.getStandardRsi()) {
            log.info("[매수 조건] RSI가 너무 높음, 지표 rsi={}, 기준 rsi={}", index.getRsi(), param.getStandardRsi());
            return false;
        }

        log.info("[매수 조건] 매수 조건 만족");
        return true;
    }

    private double getVolumeForBuy(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();

        double averagePrice = tradingResultPack.getAveragePrice();
        double lossRate = ((averagePrice - currentPrice) / averagePrice); // 현재 손실율

        return param.getInitOrderPrice() / currentPrice;
    }

    private boolean isEnoughBufferTime(LocalDateTime standard) {
        if (Objects.isNull(standard)) {
            return true;
        }
        return TradingTimeContext.now().isAfter(standard.plusMinutes(param.getConditionTimeBuffer()));
    }

    private boolean isTooOld(SpotTradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
