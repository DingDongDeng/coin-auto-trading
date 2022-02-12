package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    private final double STANDARD_OF_LOW_RSI = 0.25;
    private final double STANDARD_OF_PROFIT_RATE = 1.3;
    private final double STANDARD_OF_LOSS_RATE = 0.7;
    private final double ORDER_PRICE = 5000;
    private final double ACCOUNT_BALANCE_LIMIT = 3000000; //계좌 금액 안전 장치

    private final StrategyAssistant assistant;

    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, ExchangeService processor, StrategyAssistant assistant) {
        super(coinType, tradingTerm, processor);
        this.assistant = assistant;
    }

    @Override
    protected List<TradingTask> makeTradingTask(ExchangeTradingInfo tradingInfo) {

        log.info("tradingInfo : {}", tradingInfo);
        CoinType coinType = tradingInfo.getCoinType();
        double rsi = tradingInfo.getRsi();
        List<ExchangeOrder> undecidedOrderList = tradingInfo.getUndecidedExchangeOrderList();

        /**
         * 계좌 상태가 거래 가능한지 확인
         */
        if (!assistant.isEnoughBalance(tradingInfo.getBalance(), ACCOUNT_BALANCE_LIMIT)) {
            log.warn("계좌가 거래 가능한 상태가 아님");
            return List.of(new TradingTask());
        }

        /**
         * 미체결 상태가 너무 오래되면, 주문을 취소
         */
        List<ExchangeOrder> oldUndecidedBuyOrderList = undecidedOrderList.stream()
            .filter(o -> ChronoUnit.MINUTES.between(o.getCreatedAt(), LocalDateTime.now()) > 2)
            .collect(Collectors.toList());
        if (!oldUndecidedBuyOrderList.isEmpty()) {
            return oldUndecidedBuyOrderList.stream()
                .map(o -> TradingTask.builder().orderId(o.getOrderId()).build())
                .collect(Collectors.toList());
        }

        /**
         * 매수 주문이 체결된 후 현재 가격을 모니터링하다가 익절/손절 주문을 요청함
         */
        TradingResult buyTradingResult = assistant.findBuyTradingResult(this.getClass());
        TradingResult profitTradingResult = assistant.findProfitTradingResult(this.getClass());
        TradingResult lossTradingResult = assistant.findLossTradingResult(this.getClass());
        if (Objects.nonNull(buyTradingResult.getId()) && undecidedOrderList.stream().noneMatch(o -> o.getOrderId().equals(buyTradingResult.getOrderId()))) {
            log.info("매수 주문이 체결된 상태임");
            double currentPrice = tradingInfo.getTicker().getTradePrice();

            // 익절/손절 중복 요청 방지
            if (Objects.nonNull(profitTradingResult.getId()) || Objects.nonNull(lossTradingResult.getId())) {
                log.info("익절, 손절 주문을 했었음");

                // 익절/손절 체결된 경우 정보 초기화
                if (undecidedOrderList.isEmpty()) {
                    log.info("익절, 손절 주문이 체결되었음");
                    //매수, 익절, 손절에 대한 정보를 모두 초기화
                    assistant.delete(profitTradingResult);
                    assistant.delete(lossTradingResult);
                    assistant.delete(assistant.findBuyTradingResult(this.getClass()));
                }
                return List.of(new TradingTask());
            }

            //익절 주문
            if (currentPrice > buyTradingResult.getPrice() * STANDARD_OF_PROFIT_RATE) {
                log.info("익절 주문 요청");
                return List.of(
                    TradingTask.builder()
                        .coinType(coinType)
                        .orderType(OrderType.SELL)
                        .volume(buyTradingResult.getVolume())
                        .price(buyTradingResult.getPrice() * STANDARD_OF_PROFIT_RATE)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.PROFIT)
                        .build()
                );
            }

            //손절 주문
            if (currentPrice < buyTradingResult.getPrice() * STANDARD_OF_LOSS_RATE) {
                log.info("손절 주문 요청");
                return List.of(
                    TradingTask.builder()
                        .coinType(coinType)
                        .orderType(OrderType.SELL)
                        .volume(buyTradingResult.getVolume())
                        .price(buyTradingResult.getPrice() * STANDARD_OF_LOSS_RATE)
                        .priceType(PriceType.LIMIT_PRICE)
                        .tag(TradingTag.LOSS)
                        .build()

                );
            }

            return List.of(new TradingTask());
        }

        /**
         * rsi가 조건을 만족하고, 미체결중인 내역이 없다면 매수 주문을 요청함
         */
        if (isBuyTiming(rsi, undecidedOrderList)) {
            log.info("매수 주문 요청");
            double price = ORDER_PRICE;
            double volume = price / tradingInfo.getTicker().getTradePrice();

            return List.of(
                TradingTask.builder()
                    .coinType(coinType)
                    .orderType(OrderType.BUY)
                    .volume(volume)
                    .price(price)
                    .priceType(PriceType.LIMIT_PRICE)
                    .tag(TradingTag.BUY)
                    .build()
            );
        }

        return List.of(new TradingTask());
    }

    @Override
    protected void handleOrderResult(ExchangeOrder order, TradingResult tradingResult) {
        assistant.saveTradingResult(tradingResult);
    }

    private boolean isBuyTiming(double rsi, List<ExchangeOrder> undecidedOrderList) {
        return undecidedOrderList.isEmpty() && rsi < STANDARD_OF_LOW_RSI;
    }

}
