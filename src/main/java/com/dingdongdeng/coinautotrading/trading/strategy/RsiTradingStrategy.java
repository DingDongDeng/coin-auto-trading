package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    //fixme 백테스팅 고려하기
    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, ExchangeService processor) {
        super(coinType, tradingTerm, processor);
    }

    @Override
    protected TradingTask makeTradingTask(TradingInfo tradingInfo) {
        CoinType coinType = tradingInfo.getCoinType();
        double rsi = tradingInfo.getRsi();
        double volume = 1.0;
        double price = 5000.0;

        //미체결된 주문이 있다면??
        //체결된 주문이 있다면??
        // 손절 기준은?
        // 익절 기준은?
        if (rsi < 0.3) {
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
