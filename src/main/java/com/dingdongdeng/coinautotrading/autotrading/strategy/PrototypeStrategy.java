package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.model.OrderTask;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.exchange.processor.ExchangeProcessor;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import java.util.Stack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrototypeStrategy extends Strategy {

    //fixme 백테스팅 고려하기
    //fixme stateful 해야함 , 임베디드 db든 필요함
    public PrototypeStrategy(ExchangeProcessor processor) {
        super(processor);
    }

    @Override
    protected OrderTask makeOrderTask(ProcessAccountResult account, Stack<ProcessOrderResult> unDecidedOrderStack) {
        /**
         * 계좌의 돈을 확인한다
         *
         * 조건이 되면(rsi)
         * 주문(매수,매도)를 한다
         *
         */

        double RSI = getRsi();
        double volume = 0.0;
        double price = 0.0;
        if (RSI < 0.3) {
            //미체결된 주문이 있다면??
            //체결된 주문이 있다면??
            return OrderTask.builder()
                .coinType(CoinType.ETHEREUM)
                .orderType(OrderType.BUY)
                .volume(volume)
                .price(price)
                .priceType(PriceType.LIMIT_PRICE)
                .build();
        }

        if (RSI > 0.5) {

        }

        return OrderTask.builder().orderType(null).build();
    }

    private double getRsi() {
        return 0.0;
    }
}
