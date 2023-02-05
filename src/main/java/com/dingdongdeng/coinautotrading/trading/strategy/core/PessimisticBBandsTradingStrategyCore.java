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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PessimisticBBandsTradingStrategyCore implements StrategyCore<SpotTradingInfo, SpotTradingResult> {

    private final PessimisticBBandsTradingStrategyCoreParam param;

    // 매수,익절,손절 조건에 도달하였다고 해서 바로 수행하지 않고, 방향성 확인을 위해 버퍼 시간을 둠
    private LocalDateTime positionInitDateTime;
    private LocalDateTime positionCompletedDateTime;
    private LocalDateTime recentOutOfLowerDateTime; // 볼린저 밴드 하단 선 밖으로 나간 시간

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
        if (List.of(TradingTag.PROFIT, TradingTag.LOSS).contains(tradingResult.getTradingTag())) {
            this.positionCompletedDateTime = tradingResult.getCreatedAt();
        }
        if (tradingResult.getTradingTag() == TradingTag.BUY) {
            this.positionInitDateTime = tradingResult.getCreatedAt();
        }
    }

    @Override
    public void handleOrderCancelResult(SpotTradingResult tradingResult) {
        if (List.of(TradingTag.PROFIT, TradingTag.LOSS).contains(tradingResult.getTradingTag())) {
            this.positionCompletedDateTime = null;
        }
        if (tradingResult.getTradingTag() == TradingTag.BUY) {
            this.positionInitDateTime = null;
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
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        boolean isExsistBuyOrder = !buyTradingResultList.isEmpty();
        ExchangeCandles candles = tradingInfo.getCandles();

        double bbandsUpper = index.getBollingerBands().getUpper();
        double bbandsMiddle = index.getBollingerBands().getMiddle();
        double bbandsLower = index.getBollingerBands().getLower();
        double bbandsHeight = index.getBollingerBands().getHeight();
        double bbandsHeightHist = index.getBollingerBands().getHeightHist();

        double macdHist = index.getMacd().getHist();
        double macdSignal = index.getMacd().getSignal();
        double macdMacd = index.getMacd().getMacd();

        double bufferPrice = getBufferPrice(bbandsHeight);

        // 볼린저 밴드 하단 아래로 내려간 시간 기록
        if (bbandsLower - bufferPrice > currentPrice) {
            this.recentOutOfLowerDateTime = TradingTimeContext.now();
        }

        // 추가 매수 안함
        if (isExsistBuyOrder) {
            log.info("[추가 매수 조건] 추가 매수 안함");
            return false;
        }

        // 포지션 종료 후 유예시간을 가져야함
        if (!isEnoughBufferTime(positionCompletedDateTime)) {
            log.info("[매수 조건] 포지션 정리 후 유예시간을 가져야함, bufferTime={}, positionCompletedDateTime={}, now={}", param.getConditionTimeBuffer(), positionCompletedDateTime,
                TradingTimeContext.now());
            return false;
        }

        // 이미 상승 추세라면
        if (macdHist > 0) {
            log.info("[매수 조건] 이미 상승 추세, hist={}", macdHist);
            return false;
        }

        // 볼린저 밴드의 변동성이 점점 줄어들고 있다면
        if (bbandsHeightHist < 0) { //fixme -N보다 작을때로해보자
            log.info("[매수 조건] 볼린저 밴드의 변동성이 점점 줄어들고 있음, bbandsHeightHist={}", bbandsHeightHist);
            return false;
        }

        // 볼린저 밴드 하단으로 내려갔다면 유예시간을 가져야함(하락의 가능성이 큼)
        if (!isEnoughBufferTime(recentOutOfLowerDateTime)) {
            log.info("[매수 조건] 볼린저 밴드 하단으로 내려갔다면 유예시간을 가져야함, bufferTime={}, recentOutOfLowerDateTime={}, now={}", param.getConditionTimeBuffer(), recentOutOfLowerDateTime,
                TradingTimeContext.now());
            return false;
        }

        // 볼린저밴드 lower 근처가 아니라면
        if (bbandsLower + bufferPrice < currentPrice || bbandsLower - bufferPrice > currentPrice) {
            log.info("[매수 조건] 볼린저 밴드 하단선 근처가 아니라면, lower={}, currentPrice={}, bufferPrice={}", bbandsLower, currentPrice, bufferPrice);
            return false;
        }

        log.info("[매수 조건] 매수 조건 만족");
        return true;
    }

    private boolean isProfitOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        ExchangeCandles candles = tradingInfo.getCandles();
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);

        double macdHist = index.getMacd().getHist();
        double macdSignal = index.getMacd().getSignal();
        double macdMacd = index.getMacd().getMacd();

        double bbandsUpper = index.getBollingerBands().getUpper();
        double bbandsMiddle = index.getBollingerBands().getMiddle();
        double bbandsLower = index.getBollingerBands().getLower();
        double bbandsHeight = index.getBollingerBands().getHeight();

        double obvHist = index.getObv().getHist();

        // 매수 주문한적이 없다면
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            log.info("[익절 조건] 매수 주문 한적이 없음");
            return false;
        }

        // 포지션 진입 후 유예시간을 충분히 갖지 않았다면
        LocalDateTime lastBuyOrderDateTime = lastBuyTradingResult.getCreatedAt();
        if (!isEnoughBufferTime(lastBuyOrderDateTime)) {
            log.info("[익절 조건] 방향성이 나올때까지 충분한 유예시간을 갖지 못함, lastBuyOrderDateTime={}, currentDateTime={}", lastBuyOrderDateTime, TradingTimeContext.now());
            return false;
        }

        // 손실중이면
        if (currentPrice <= tradingResultPack.getAveragePrice()) {
            log.info("[익절 조건] 손실 중, currentPrice={}, averagePrice={}", currentPrice, tradingResultPack.getAveragePrice());
            return false;
        }

        // 목표 저항선까지 도달하지 않았다면
        double targetProfitPrice = bbandsMiddle - getBufferPrice(bbandsHeight, 0.1);
        if (targetProfitPrice > currentPrice) {
            log.info("[익절 조건] 저항선에 도달하지 않으면 익절하지 않음, targetProfitPrice={}, currentPrice={}", targetProfitPrice, currentPrice);
            return false;
        }

        log.info("[익절 조건] 익절 조건 만족");
        return true;
    }

    private boolean isLossOrderTiming(double currentPrice, TradingInfo tradingInfo, TradingResultPack<SpotTradingResult> tradingResultPack, Index index) {
        List<SpotTradingResult> buyTradingResultList = tradingResultPack.getBuyTradingResultList();
        SpotTradingResult lastBuyTradingResult = buyTradingResultList.get(buyTradingResultList.size() - 1);

        double macdHist = index.getMacd().getHist();
        double macdSignal = index.getMacd().getSignal();
        double macdMacd = index.getMacd().getMacd();

        double bbandsUpper = index.getBollingerBands().getUpper();
        double bbandsMiddle = index.getBollingerBands().getMiddle();
        double bbandsLower = index.getBollingerBands().getLower();
        double bbandsHeight = index.getBollingerBands().getHeight();

        double bufferPrice = getBufferPrice(bbandsHeight, 0.1);

        // 매수 주문한적이 없다면
        if (tradingResultPack.getBuyTradingResultList().isEmpty()) {
            log.info("[손절 조건] 매수 주문한적이 없음");
            return false;
        }

        // 포지션 진입 후 유예시간을 충분히 갖지 않았다면
        LocalDateTime lastBuyOrderDateTime = lastBuyTradingResult.getCreatedAt();
        if (!isEnoughBufferTime(lastBuyOrderDateTime)) {
            log.info("[손절 조건] 방향성이 나올때까지 충분한 유예시간을 갖지 못함, lastBuyOrderDateTime={}, currentDateTime={}", lastBuyOrderDateTime, TradingTimeContext.now());
            return false;
        }

        // 손실중이 아니라면
        if (currentPrice > tradingResultPack.getAveragePrice()) {
            log.info("[손절 조건] 손실 중이 아님, currentPrice={}, averagePrice={}", currentPrice, tradingResultPack.getAveragePrice());
            return false;
        }

        // 가격이 5만원 이상 떨어졌다면 바로 손절
        if (tradingResultPack.getAveragePrice() - currentPrice > tradingResultPack.getAveragePrice() * 0.03) {
            log.info("[손절 조건] 손절 조건 만족, 가격이 크게 떨어짐, averagePrice={}, currentPrice={}", tradingResultPack.getAveragePrice(), currentPrice);
            return true;
        }

        // 지지받고 있다면
        if (bbandsLower - bufferPrice < currentPrice) {
            log.info("[손절 조건] 지지 받고 있음, lower={}, bufferPrice={}, currentPrice={}", bbandsLower, bufferPrice, currentPrice);
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

    private boolean isEnoughBufferTime(LocalDateTime standard) {
        if (Objects.isNull(standard)) {
            return true;
        }
        return TradingTimeContext.now().isAfter(standard.plusMinutes(param.getConditionTimeBuffer()));
    }

    private double getBufferPrice(double bbandsHeight, double rate) {
        return bbandsHeight * rate;
    }

    private double getBufferPrice(double bbandsHeight) {
        return (bbandsHeight / 10) > 20000 ? 20000 : 10000;
    }

    private boolean isTooOld(SpotTradingResult tradingResult) {
        if (Objects.isNull(tradingResult.getCreatedAt())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(tradingResult.getCreatedAt(), TradingTimeContext.now()) >= param.getTooOldOrderTimeSeconds();
    }
}
