package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeTradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    //fixme 백테스팅 고려하기
    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, ExchangeService processor) {
        super(coinType, tradingTerm, processor);
    }

    @Override
    protected TradingTask makeTradingTask(ExchangeTradingInfo tradingInfo) {
        CoinType coinType = tradingInfo.getCoinType();
        double balance = tradingInfo.getBalance();
        double rsi = tradingInfo.getRsi();

        double volume = 1.0;
        double price = 5000.0;

        /**
         * 계좌에 충분한 돈이 있는지 확인
         * 미체결된 주문이 아무것도 없고, rsi가 0.25 미만이면 현재 가격으로 지정가 매수 주문을 한다.
         *
         * 매수주문이 체결상태면
         * 손실금액이 30%지점에 지정가 손절 매도주문을 한다
         * 이익금액이 30%지점에 지정가 익절 매도주문을 한다
         *
         * 매수주문이 미체결상태면
         * 주문을 넣은지 10초가 지났고, rsi가 0.25미만이면 주문을 취소한다
         *
         * 손절or익절 매도 주문이 체결되면 모든 포지션을 정리한다
         *
         *
         * optional
         * //하락장 방어로직
         * 손절이 연속되면, 현재가를 저장하고 매매를 멈춘다
         * 이전에 저장한 현재가보다 가격이 20%높아지면 다시 매매를 시작한다
         */

        //미체결된 주문이 있다면??
        //체결된 주문이 있다면??
        // 손절 기준은?
        // 익절 기준은?
        if (rsi < 0.25) {
            return TradingTask.builder()
                .coinType(coinType)
                .orderType(OrderType.BUY)
                .volume(volume)
                .price(price)
                .priceType(PriceType.LIMIT_PRICE)
                .build();
        }

        if (rsi > 0.5) {
            return TradingTask.builder()
                .coinType(coinType)
                .orderType(OrderType.SELL)
                .volume(volume)
                .price(price)
                .priceType(PriceType.LIMIT_PRICE)
                .build();
        }

        return TradingTask.builder().build();
    }
}
