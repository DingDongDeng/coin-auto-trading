package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.common.context.TradingTimeContext;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.index.Index;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.SpotTradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
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
            if (isProfitOrderTiming(currentPrice, tradingInfo, tradingResultPack, index)) {
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
            if (isLossOrderTiming(currentPrice, tradingInfo, tradingResultPack, index)) {
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

    private boolean isBuyOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        boolean isExsistBuyOrder = !buyTradingResultList.isEmpty();
        ExchangeCandles candles = tradingInfo.getCandles();

        double movingAveragePrice = this.getMovingAveragePrice(candles);
        double movingResistancePrice = this.getResistancePrice(movingAveragePrice, index);
        double movingSupportPrice = this.getSupportPrice(movingAveragePrice, 0, index);
        double movingPrevSupportPrice = this.getSupportPrice(movingAveragePrice, 1, index);

        // 추가 매수 안함
        if (isExsistBuyOrder) {
            log.info("[추가 매수 조건] 추가 매수 안함");
            return false;
        }

        // 하락 추세라면
        if (index.getMacd().getCurrent() < 1000 || index.getRsi() < 0.30) {
            log.info("[매수 조건] 하락 추세, macd={}, rsi={}", index.getMacd().getCurrent(), index.getRsi());
            return false;
        }

        // 저항 받고 있다면
        if (movingAveragePrice < movingResistancePrice && movingAveragePrice > (movingResistancePrice - param.getResistancePriceBuffer())) {
            log.info("[매수 조건] 저항 받고 있음, resistancePriceList={}, averagePrice={}", index.getResistancePriceList(), movingAveragePrice);
            return false;
        }

        // 상승 추세가 약해지고 있다면
        if (index.getMacd().getCurrentUptrendHighest() * 0.8 > index.getMacd().getCurrent()) {
            log.info("[매수 조건] 상승 추세가 약해지고 있음, currentUptrendHighest={}, macdCurrent={}", index.getMacd().getCurrentUptrendHighest(), index.getMacd().getCurrent());
            return false;
        }

        // 지지선 아래를 바쳐주는 지지선이 없다면(리스크가 너무 큼)
        if (movingPrevSupportPrice == 0) {
            log.info("[매수 조건] 지지선 아래에 다른 지지선이 없음, resistancePriceList={}, movingPrevSupportPrice={}, movingAveragePrice={}", index.getResistancePriceList(), movingPrevSupportPrice,
                movingAveragePrice);
            return false;
        }

        // 지지 받고 있지 않다면
        if (movingAveragePrice > (movingSupportPrice + param.getResistancePriceBuffer()) || movingAveragePrice < movingSupportPrice) {
            // 저항선이 없음
            if (movingResistancePrice == Integer.MAX_VALUE) {
                log.info("[매수 조건] 매수 조건 만족, 저항선이 없음");
                return true;
            }
            log.info("[매수 조건] 지지 받고 있지 않음, resistancePriceList={}, averagePrice={}", index.getResistancePriceList(), movingAveragePrice);
            return false;
        }

        // 주문한적이 있다면
        if (isExsistBuyOrder) {
            if (buyTradingResultList.size() > 1) {
                log.info("[추가 매수 조건] 이미 추가 매수를 하였음");
                return false;
            }

            // 이익중이라면
            if ((tradingResultPack.getAveragePrice() - currentPrice) < 0) {
                log.info("[추가 매수 조건] 이익중, averagePrice={}, currentPrice={}", tradingResultPack.getAveragePrice(), currentPrice);
                return false;
            }

            // 마지막 주문보다 현재가가 높다면(추가 매수는 항상 더 낮은 가격으로 사야하기 때문)
            SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);
            if (lastBuyTradingResult.getPrice() <= currentPrice) {
                log.info("[추가 매수 조건] 추가 매수하기에는 현재가가 높음, lastBuyTradingResult.price={}, currentPrice={}", lastBuyTradingResult.getPrice(), currentPrice);
                return false;
            }

            // 마지막으로 매수했던 지지선의 바로 아래 지지선이 아니라면
            double prevSupportPrice_ = this.getSupportPrice(lastBuyTradingResult.getPrice(), 1, index);
            if (movingAveragePrice < prevSupportPrice_ || movingAveragePrice > (prevSupportPrice_ + param.getResistancePriceBuffer())) {
                log.info("[추가 매수 조건] 마지막 매수의 아래 아래 지지선이 아님, movingAveragePrice={}, lastBuyTradingResult.price={}, param.resistancePriceBuffer={}", movingAveragePrice,
                    lastBuyTradingResult.getPrice(),
                    param.getResistancePriceBuffer());
                return false;
            }
            log.info("[추가 매수 조건] 추가 매수 조건 만족");
            return true;
        }

        log.info("[매수 조건] 매수 조건 만족");
        return true;
    }

    private boolean isProfitOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);
        double resistancePrice = this.getResistancePrice(lastBuyTradingResult.getPrice(), index);
        double supportPrice = this.getSupportPrice(lastBuyTradingResult.getPrice(), 0, index);
        double prevSupportPrice = this.getSupportPrice(lastBuyTradingResult.getPrice(), 1, index);
        double profitPotential = resistancePrice - supportPrice;
        double lossPotential = supportPrice - prevSupportPrice;

        double movingAveragePrice = this.getMovingAveragePrice(tradingInfo.getCandles());
        double movingSupportPrice = this.getSupportPrice(movingAveragePrice, 0, index);
        double movingResistancePrice = this.getResistancePrice(movingAveragePrice, index);
        double movingPrevSupportPrice = this.getSupportPrice(movingAveragePrice, 1, index);
        double movingProfitPotential = movingResistancePrice - movingSupportPrice;
        double movingLossPotential = movingSupportPrice - movingPrevSupportPrice;

        // 매수 주문한적이 없다면
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            log.info("[익절 조건] 매수 주문 한적이 없음");
            return false;
        }

        // 손실중이면
        if (currentPrice <= tradingResultPack.getAveragePrice()) {
            log.info("[익절 조건] 손실 중, currentPrice={}, averagePrice={}", currentPrice, tradingResultPack.getAveragePrice());
            return false;
        }

        // 상승 추세가 아직 유지되고 있다면
        double rate = movingLossPotential > movingProfitPotential ? 0.8 : 0.6;
        if (index.getMacd().getCurrentUptrendHighest() * rate < index.getMacd().getCurrent()) {
            log.info("[익절 조건] 상승 추세가 유지되고 있음, currentUptrendHighest={}, macd={}", index.getMacd().getCurrentUptrendHighest(), index.getMacd().getCurrent());
            return false;
        }

        // 최소 익절금액은 감수했던 손절 리스크보다는 커야함
        if (prevSupportPrice != 0 && ((currentPrice - tradingResultPack.getAveragePrice()) < lossPotential)) {
            log.info("[익절 조건] 최소 익절 금액이 감수했던 리스크 금액보다 작음 , 익절금액={}, lossPotential={}", currentPrice - tradingResultPack.getAveragePrice(), lossPotential);
            return false;
        }

        log.info("[익절 조건] 익절 조건 만족");
        return true;
    }

    private boolean isLossOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);
        double prevSupportPrice = this.getSupportPrice(lastBuyTradingResult.getPrice(), 1, index);
        double movingAveragePrice = this.getMovingAveragePrice(tradingInfo.getCandles());
        double movingResistancePrice = this.getResistancePrice(movingAveragePrice, index);
        double movingSupportPrice = this.getSupportPrice(movingAveragePrice, 0, index);
        double movingPrevSupportPrice = this.getSupportPrice(movingAveragePrice, 1, index);
        double movingProfitPotential = movingResistancePrice - movingSupportPrice;
        double movingLossPotential = movingSupportPrice - movingPrevSupportPrice;

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

        // 두번째 지지선까지 하락하지 않았다면
        if (movingAveragePrice > prevSupportPrice) {
            log.info("[손절 조건] 두번째 지지선까지는 하락하지 않음, resistancePriceList={}, movingAveragePrice={}", index.getResistancePriceList(), movingAveragePrice);
            return false;
        }

        // 상승 추세가 아직 유지되고 있다면
        double rate = movingLossPotential > movingProfitPotential ? 0.8 : 0.6;
        if (index.getMacd().getCurrentUptrendHighest() * rate < index.getMacd().getCurrent()) {
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

    private double getSupportPrice(double standardPrice, int next, Index index) {
        int nextCount = 0;
        for (int i = index.getResistancePriceList().size() - 1; i >= 0; i--) {
            double supportPrice = index.getResistancePriceList().get(i);
            if (standardPrice > supportPrice) {
                if (nextCount < next) {
                    nextCount++;
                    continue;
                }
                return supportPrice;
            }

        }
        log.warn("지지선 찾지 못함");
        return 0;
    }

    private double getResistancePrice(double standardPrice, Index index) {
        for (Double resistancePrice : index.getResistancePriceList()) {
            if (standardPrice < resistancePrice) {
                return resistancePrice;
            }
        }
        log.warn("저항선 찾지 못함");
        return Integer.MAX_VALUE;
    }

    private double getMovingAveragePrice(ExchangeCandles candles) {
        double averagePrice = (
            candles.getLatest(0).getTradePrice() + candles.getLatest(0).getOpeningPrice()
                + candles.getLatest(1).getTradePrice() + candles.getLatest(1).getOpeningPrice()
                + candles.getLatest(2).getTradePrice() + candles.getLatest(2).getOpeningPrice()
        ) / 6.0;

        return averagePrice;
    }

    private boolean isTooOld(SpotTradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
