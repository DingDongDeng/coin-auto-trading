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

        double macdHist = index.getMacd().getHist();
        double macdSignal = index.getMacd().getSignal();
        double macdMacd = index.getMacd().getMacd();

        double resistancePrice = this.getResistancePrice(currentPrice, index);
        double supportPrice = this.getSupportPrice(currentPrice, 0, index);
        double prevSupportPrice = this.getSupportPrice(currentPrice, 1, index);

        // 추가 매수 안함
        if (isExsistBuyOrder) {
            log.info("[추가 매수 조건] 추가 매수 안함");
            return false;
        }

        // 상승 추세가 아니라면
        if (macdHist < 0) {
            log.info("[매수 조건] 하락 추세, hist={}", macdHist);
            return false;
        }

        // macd가 이미 상승중이라면 매수하지 않음
        if (index.getMacd().getLatestHist(1) > 100) {
            log.info("[매수 조건] 하락에서 상승으로 이미 전환되었다면 매수하지 않음, macd={}, prevMacd={}", macdHist, index.getMacd().getLatestHist(1));
            return false;
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

        // 방향성이 나오지 않았다면
        if (!isDecidedDirection(currentPrice, lastBuyTradingResult.getPrice())) {
            log.info("[익절 조건] 방향성이 나오지 않음, hist={}", index.getMacd().getHist());
            return false;
        }

        if (index.getMacd().getMacd() < 0) {
            // 상승 추세 유지 중 (hist)
            double macdHist = index.getMacd().getHist();
            double uptrendHighestHist = index.getMacd().getCurrentUptrendHighestHist();
            if (uptrendHighestHist * 0.9 < macdHist) {
                log.info("[익절 조건] 상승 추세 유지 중, uptrendHighestHist={}, macdHist={}", uptrendHighestHist, macdHist);
                return false;
            }
        } else {
            // 상승 추세 유지 중 (macd)
            double macdMacd = index.getMacd().getMacd();
            double uptrendHighestMacd = index.getMacd().getCurrentUptrendHighestMacd();
            if (uptrendHighestMacd * 0.9 < macdMacd) {
                log.info("[익절 조건] 상승 추세 유지 중, uptrendHighestMacd={}, macdMacd={}", uptrendHighestMacd, macdMacd);
                return false;
            }
        }

        log.info("[익절 조건] 익절 조건 만족");
        return true;
    }

    private boolean isLossOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);

        double resistancePrice = this.getResistancePrice(lastBuyTradingResult.getPrice(), index);
        double supportPrice = this.getSupportPrice(lastBuyTradingResult.getPrice(), 0, index);
        double prevSupportPrice = this.getSupportPrice(lastBuyTradingResult.getPrice(), 1, index);
        double profitPotential = resistancePrice - supportPrice;
        double lossPotential = supportPrice - prevSupportPrice;

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

        // 방향성이 나오지 않았다면
        if (!isDecidedDirection(currentPrice, lastBuyTradingResult.getPrice())) {
            log.info("[손절 조건] 방향성이 나오지 않음, hist={}", index.getMacd().getHist());
            return false;
        }

        if (index.getMacd().getMacd() < 0) {
            // 상승 추세 유지 중 (hist)
            double macdHist = index.getMacd().getHist();
            double uptrendHighestHist = index.getMacd().getCurrentUptrendHighestHist();
            if (uptrendHighestHist * 0.9 < macdHist) {
                log.info("[손절 조건] 상승 추세 유지 중, uptrendHighestHist={}, macdHist={}", uptrendHighestHist, macdHist);
                return false;
            }
        } else {
            // 상승 추세가 유지 중 (macd)
            double macdMacd = index.getMacd().getMacd();
            double uptrendHighestMacd = index.getMacd().getCurrentUptrendHighestMacd();
            if (uptrendHighestMacd * 0.9 < macdMacd) {
                log.info("[손절 조건] 상승 추세 유지 중, uptrendHighestMacd={}, macdMacd={}", uptrendHighestMacd, macdMacd);
                return false;
            }
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

    private boolean isDecidedDirection(double currentPrice, double buyPrice) {
        return Math.abs(currentPrice - buyPrice) > 30000;
    }

    private boolean isTooOld(SpotTradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
