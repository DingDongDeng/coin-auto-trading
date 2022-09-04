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
public class ResistanceTradingStrategyCore implements StrategyCore<SpotTradingInfo, SpotTradingResult> {

    private final ResistanceTradingStrategyCoreParam param;

    /*
     *  저항선 기반 매매
     *
     *  매수 시점
     *  - 하락추세가 아닐때
     *  - 저항선에 지지 받을때
     *
     *  익절 시점
     *  - 저항선에 저항 받을때
     *
     *  손절 시점
     *  - 하지 않음
     */
    @Override
    public List<TradingTask> makeTradingTask(SpotTradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack) {
        String identifyCode = tradingInfo.getIdentifyCode();
        CoinType coinType = tradingInfo.getCoinType();
        TradingTerm tradingTerm = tradingInfo.getTradingTerm();
        Index index = tradingInfo.getIndex();

        log.info("tradingInfo : {}", tradingInfo);

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

        /*
         * 매수 주문이 체결된 후 현재 가격을 모니터링하다가 익절/손절 주문을 요청함
         */
        if (!buyTradingResultList.isEmpty()) {
            log.info(":: 매수 주문이 체결된 상태임");
            double currentPrice = tradingInfo.getCurrentPrice();

            if (!profitTradingResultList.isEmpty() || !lossTradingResultList.isEmpty()) {
                log.info(":: 익절, 손절 주문이 체결되었음");
                //매수, 익절, 손절에 대한 정보를 모두 초기화
                return List.of(
                    TradingTask.builder().isReset(true).build()
                );
            }

            //익절 주문
            if (isProfitOrderTiming(currentPrice, tradingResultPack, index)) {
                log.info(":: 익절 주문 요청");
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderType(OrderType.SELL)
                        .volume(tradingResultPack.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT)
                        .tag(TradingTag.PROFIT)
                        .build()
                );
            }

            //손절 주문
            if (isLossOrderTiming(currentPrice, tradingResultPack, index)) {
                log.info(":: 부분 또는 전부 손절 주문 요청");
                return List.of(
                    TradingTask.builder()
                        .identifyCode(identifyCode)
                        .coinType(coinType)
                        .tradingTerm(tradingTerm)
                        .orderType(OrderType.SELL)
                        .volume(tradingResultPack.getVolume())
                        .price(currentPrice)
                        .priceType(PriceType.LIMIT)
                        .tag(TradingTag.LOSS)
                        .build()
                );
            }
        }

        /*
         * 조건을 만족하면 매수 주문
         */
        if (isBuyOrderTiming(tradingInfo.getCurrentPrice(), tradingResultPack, index)) {
            if (!isEnoughBalance(tradingInfo.getCurrentPrice(), tradingResultPack, tradingInfo.getBalance())) {
                log.warn(":: 계좌가 매수 가능한 상태가 아님");
                return List.of(new TradingTask());
            }

            log.info(":: 매수 주문 요청");
            double currentPrice = tradingInfo.getCurrentPrice();
            double volume = getVolumeForBuy(currentPrice, tradingResultPack);
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
    public void handleOrderResult(SpotTradingResult tradingResult) {

    }

    @Override
    public void handleOrderCancelResult(SpotTradingResult tradingResult) {

    }

    @Override
    public StrategyCoreParam getParam() {
        return this.param;
    }

    private boolean isEnoughBalance(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, double balance) {
        if (balance < param.getAccountBalanceLimit()) {
            return false;
        }

        if (tradingResultPack.getVolume() * currentPrice > balance) {
            return false;
        }

        return true;
    }

    private boolean isBuyOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        boolean isExsistBuyOrder = !buyTradingResultList.isEmpty();
        boolean isResistancePrice = index.getResistancePriceList().stream()
            .anyMatch(resistancePrice -> currentPrice < resistancePrice * (1 + param.getResistancePriceBuffer()) && currentPrice > resistancePrice);

        // 하락 추세라면
        if (index.getRsi() < 0.45 || index.getMacd() < 0) {
            return false;
        }

        // 지지받고 있지 않다면
        if (!isResistancePrice) {
            return false;
        }

        // 주문한적이 있다면
        if (isExsistBuyOrder) {

            // 이익중이라면
            if ((tradingResultPack.getAveragePrice() - currentPrice) < 0) {
                return false;
            }

            SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);
            // 마지막 주문보다 현재가가 높다면(추가 매수는 항상 더 낮은 가격으로 사야하기 때문)
            if (lastBuyTradingResult.getPrice() <= currentPrice) {
                return false;
            }

            // 마지막 주문의 가격 ~ 마지막 주문의 지지선 가격 사이는 주문하지 않음(같은 구간에 대해서 중복 주문이기 때문)
            if (currentPrice <= lastBuyTradingResult.getPrice() && currentPrice >= lastBuyTradingResult.getPrice() * (1 - param.getResistancePriceBuffer())) {
                return false;
            }
        }

        return true;
    }

    private boolean isProfitOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        boolean isResistancePrice = index.getResistancePriceList().stream()
            .anyMatch(resistancePrice -> currentPrice > resistancePrice * (1 - param.getResistancePriceBuffer()) && currentPrice < resistancePrice);

        // 손실중이면
        if (currentPrice < tradingResultPack.getAveragePrice()) {
            return false;
        }

        // 저항을 받는중이 아니면(더 오를 가능성이 있다면)
        if (!isResistancePrice) {
            return false;
        }

        // 아직 상승 추세라면
        //if (index.getMacd() > 0) {
        //    return false;
        //}

        return true;
    }

    private boolean isLossOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {

        // 손절안함
        if (true) {
            return false;
        }

        boolean isResistancePrice = index.getResistancePriceList().stream()
            .anyMatch(resistancePrice -> currentPrice < resistancePrice * (1 - param.getResistancePriceBuffer() / 2)
                && currentPrice > resistancePrice * (1 - param.getResistancePriceBuffer()));

        //손실중이 아니라면
        if (currentPrice > tradingResultPack.getAveragePrice()) {
            return false;
        }

        // 지지를 받고 있다면 (더 안내려가고 오를 가능성이 있다면)
        if (!isResistancePrice) {
            return false;
        }

        return true;
    }

    private double getVolumeForBuy(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();

        double averagePrice = tradingResultPack.getAveragePrice();
        double lossRate = ((averagePrice - currentPrice) / averagePrice); // 현재 손실율

        return param.getInitOrderPrice() / currentPrice;
    }

    private boolean isTooOld(SpotTradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
