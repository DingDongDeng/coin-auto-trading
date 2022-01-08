package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.autotrading.strategy.model.TradingInfo;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsiTradingStrategy extends Strategy {

    //fixme 백테스팅 고려하기
    public RsiTradingStrategy(CoinType coinType, TradingTerm tradingTerm, ExchangeProcessor processor) {
        super(coinType, tradingTerm, processor);
    }

    @Override
    protected OrderTask makeOrderTask(TradingInfo tradingInfo) {
        CoinType coinType = tradingInfo.getCoinType();
        double rsi = tradingInfo.getRsi();
        double volume = 1.0;
        double price = 5000.0;

        if (rsi < 0.3) {
            //미체결된 주문이 있다면??
            //체결된 주문이 있다면??
            return OrderTask.builder()
                .coinType(coinType)
                .orderType(OrderType.BUY)
                .volume(volume)
                .price(price)
                .priceType(PriceType.LIMIT_PRICE)
                .build();
        }

        if (rsi > 0.5) {
            return OrderTask.builder()
                .coinType(coinType)
                .orderType(OrderType.SELL)
                .volume(volume)
                .price(price)
                .priceType(PriceType.LIMIT_PRICE)
                .build();
        }

        return OrderTask.builder().build();
    }
}
