package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit.UnitType;
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
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    private final String IDENTIFY_CODE = StrategyCode.RSI.name() + ":" + UUID.randomUUID().toString();
    private final double BUY_RSI = 0.25; // 매수 주문을 할 rsi 기준
    private final double PROFIT_RSI = 0.50; // 이익중일때 익절할 rsi 기준
    private final double LOSS_RSI = 0.40; // 손실중일때 손절할 rsi 기준
    private final double PROFIT_LIMIT_PRICE_RATE = 0.02; // 익절 이익율 상한
    private final double LOSS_LIMIT_PRICE_RATE = 0.01; // 손절 손실율 상한
    private final int TOO_OLD_ORDER_TIME_SECONDS = 30; // 초(second)
    private final double ORDER_PRICE = 10000;
    private final double ACCOUNT_BALANCE_LIMIT = 300 * 10000; //계좌 금액 안전 장치

    private final StrategyAssistant assistant;

    @Builder
    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, String keyPairId, ExchangeService processor, StrategyAssistant assistant) {
        super(coinType, tradingTerm, keyPairId, processor);
        this.assistant = assistant;
    }

    @Override
    protected List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo) {
        log.info("{} :: ---------------------------------------", getIdentifyCode());

        CoinType coinType = tradingInfo.getCoinType();
        TradingTerm tradingTerm = tradingInfo.getTradingTerm();
        double rsi = tradingInfo.getRsi();

        log.info("tradingInfo : {}", tradingInfo);
        log.info("{} :: coinType={}", getIdentifyCode(), coinType);
        log.info("{} :: rsi={}", getIdentifyCode(), rsi);

        // 자동매매 중 기억해야할 실시간 주문 정보(익절, 손절, 매수 주문 정보)
        TradingResultPack tradingResultPack = assistant.syncedTradingResultPack(getIdentifyCode());
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
                log.info("{} :: 미체결 상태의 오래된 주문을 취소", getIdentifyCode());
                return List.of(
                    TradingTask.builder()
                        .identifyCode(getIdentifyCode())
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
            log.info("{} :: 매수 주문이 체결된 상태임", getIdentifyCode());
            double currentPrice = tradingInfo.getTicker().getTradePrice();

            // 익절/손절 중복 요청 방지
            if (profitTradingResult.isExist() || lossTradingResult.isExist()) {
                log.info("{} :: 익절, 손절 주문을 했었음", getIdentifyCode());

                // 익절/손절 체결된 경우 정보 초기화
                if (profitTradingResult.isDone() || lossTradingResult.isDone()) {
                    log.info("{} :: 익절, 손절 주문이 체결되었음", getIdentifyCode());
                    //매수, 익절, 손절에 대한 정보를 모두 초기화
                    assistant.reset(getIdentifyCode());
                }
                return List.of(new TradingTask());
            }

            //익절 주문
            if (isProfitOrderTiming(currentPrice, rsi, buyTradingResult)) {
                log.info("{} :: 익절 주문 요청", getIdentifyCode());
                return List.of(
                    TradingTask.builder()
                        .identifyCode(getIdentifyCode())
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
                log.info("{} :: 손절 주문 요청", getIdentifyCode());
                return List.of(
                    TradingTask.builder()
                        .identifyCode(getIdentifyCode())
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
                log.warn("{} :: 계좌가 매수 가능한 상태가 아님", getIdentifyCode());
                return List.of(new TradingTask());
            }

            log.info("{} :: 매수 주문 요청", getIdentifyCode());
            double price = tradingInfo.getTicker().getTradePrice();
            double volume = ORDER_PRICE / price;
            return List.of(
                TradingTask.builder()
                    .identifyCode(getIdentifyCode())
                    .coinType(coinType)
                    .tradingTerm(tradingTerm)
                    .orderType(OrderType.BUY)
                    .volume(volume)
                    .price(price)
                    .priceType(PriceType.LIMIT_PRICE)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }

        log.info("{} :: 아무것도 하지 않음", getIdentifyCode());
        return List.of(new TradingTask());
    }

    @Override
    protected void handleOrderResult(ExchangeOrder order, TradingResult tradingResult) {
        assistant.storeTradingResult(tradingResult); // 주문 성공 건 정보 저장

        // 급격한 하락장에서 연속적인 손절을 막기 위한 지연 로직
        if (tradingResult.getTag() == TradingTag.LOSS) {
            CandleUnit unit = tradingResult.getTradingTerm().getCandleUnit();
            delay(unit.getSize(), unit.getUnitType());
        }
    }

    @Override
    protected void handleOrderCancelResult(ExchangeOrderCancel orderCancel, TradingResult tradingResult) {
        assistant.reset(tradingResult); // 주문 취소 건 정보 제거
    }

    @Override
    public String getIdentifyCode() {
        return this.IDENTIFY_CODE;
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

    private void delay(int unitSize, UnitType unitType) {
        try {
            // RSI14를 참조하기 때문에 새로운 캔들 14개가 생성된 이후에 다시 매매를 하도록함(하락흐름에서의 연속적인 손절을 방지하기 위함)
            long delaySize = unitType == UnitType.MIN ? 14 * unitSize * 60 * 1000 : 14 * 10 * 60 * 1000;  //fixme 일봉, 주봉에 대한 고려
            log.info("{} :: {}ms 동안 대기 ", getIdentifyCode(), delaySize);
            Thread.sleep(delaySize);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
