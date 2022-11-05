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
    public void handleOrderResult(SpotTradingResult tradingResult) {

    }

    @Override
    public void handleOrderCancelResult(SpotTradingResult tradingResult) {

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

    private boolean isBuyOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        boolean isExsistBuyOrder = !buyTradingResultList.isEmpty();
        boolean isResistancePrice = index.getResistancePriceList().stream()
            .anyMatch(resistancePrice -> currentPrice < resistancePrice * (1 + param.getResistancePriceBuffer()) && currentPrice > resistancePrice);

        // 하락 추세라면
        if (index.getMacd().getCurrent() < 1000 || index.getRsi() < 0.30) {
            log.info("[매수 조건] 하락 추세, macd={}, rsi={}", index.getMacd().getCurrent(), index.getRsi());
            return false;
        }

        // 과열 상태라면
        if (index.getRsi() > 0.7) {
            log.info("[매수 조건] 과열 상태, rsi={}", index.getRsi());
            return false;
        }

        // 지지받고 있지 않다면
        if (!isResistancePrice) {
            log.info("[매수 조건] 지지 받고 있지 않음, resistancePriceList={}, currentPrice={}", index.getResistancePriceList(), currentPrice);
            return false;
        }

        // 상승 추세가 약해지고 있다면
        if (index.getMacd().getCurrentUptrendHighest() * 0.8 > index.getMacd().getCurrent()) {
            log.info("[매수 조건] 상승 추세가 약해지고 있음, currentUptrendHighest={}, macdCurrent={}", index.getMacd().getCurrentUptrendHighest(), index.getMacd().getCurrent());
            return false;
        }

        // 주문한적이 있다면
        if (isExsistBuyOrder) {
            /**
             * FIXME
             *  아래 추가 매수 조건에서, 중복 매수가 되는 케이스가 있음
             *  중복 주문 방지 로직이 기능이 못하는 것으로 짐작됨
             *  관련 케이스 해결 필요 그때까지 추가 매수 관련 조건은 막도록함
             */
            if (true) {
                log.info("[매수 조건] 추가 매수 하지 않음");
                return false;
            }

            // 이익중이라면
            if ((tradingResultPack.getAveragePrice() - currentPrice) < 0) {
                log.info("[매수 조건] 이익중, averagePrice={}, currentPrice={}", tradingResultPack.getAveragePrice(), currentPrice);
                return false;
            }

            SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);
            // 마지막 주문보다 현재가가 높다면(추가 매수는 항상 더 낮은 가격으로 사야하기 때문)
            if (lastBuyTradingResult.getPrice() <= currentPrice) {
                log.info("[매수 조건] 추가 매수하기에는 현재가가 높음, lastBuyTradingResult.price={}, currentPrice={}", lastBuyTradingResult.getPrice(), currentPrice);
                return false;
            }

            // 마지막 주문의 가격 ~ 마지막 주문의 지지선 가격 사이는 주문하지 않음
            // - 같은 구간에 대한 무의미한 중복 매수를 방어하기 위함
            // - 중복 주문 필터를 위해 버퍼를 두었음
            if (currentPrice <= lastBuyTradingResult.getPrice() * (1 + param.getResistancePriceBuffer())
                && currentPrice >= lastBuyTradingResult.getPrice() * (1 - param.getResistancePriceBuffer())) {
                log.info("[매수 조건] 중복 주문 방지, currentPrice={}, lastBuyTradingResult.price={}, param.resistancePriceBuffer={}", currentPrice, lastBuyTradingResult.getPrice(),
                    param.getResistancePriceBuffer());
                return false;
            }
        }

        log.info("[매수 조건] 매수 조건 만족");
        return true;
    }

    private boolean isProfitOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {

        // 매수 주문한적이 없다면
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            log.info("[익절 조건] 매수 주문 한적이 없음");
            return false;
        }

        // 손실중이면
        if (currentPrice < tradingResultPack.getAveragePrice()) {
            log.info("[익절 조건] 손실 중, currentPrice={}, averagePrice={}", currentPrice, tradingResultPack.getAveragePrice());
            return false;
        }

        // 상승 추세가 아직 유지되고 있다면
        if (index.getMacd().getCurrentUptrendHighest() * 0.8 < index.getMacd().getCurrent()) {
            log.info("[익절 조건] 상승 추세가 유지되고 있음, currentUptrendHighest={}, macd={}", index.getMacd().getCurrentUptrendHighest(), index.getMacd().getCurrent());
            return false;
        }

        log.info("[익절 조건] 익절 조건 만족");
        return true;
    }

    private boolean isLossOrderTiming(double currentPrice, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {

        // 매수 주문한적이 없다면
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            log.info("[손절 조건] 매수 주문한적이 없음");
            return false;
        }

        // 손실중이 아니라면
        if (currentPrice > tradingResultPack.getAveragePrice()) {
            log.info("[손절 조건] 손실 중이 아님, currentPrice={}, averagePrice={}", currentPrice, tradingResultPack.getAveragePrice());
            return false;
        }

        // 상승 추세가 아직 유지되고 있다면
        if (index.getMacd().getCurrentUptrendHighest() * 0.7 < index.getMacd().getCurrent()) {
            log.info("[손절 조건] 상승 추세가 유지되고 있음, currentUptrendHighest={}, macd={}", index.getMacd().getCurrentUptrendHighest(), index.getMacd().getCurrent());
            return false;
        }

        log.info("[손절 조건] 손절 조건 만족");
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
