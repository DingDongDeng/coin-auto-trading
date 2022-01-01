package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultAccount;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessResultOrderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitExchangeProcessor implements ExchangeProcessor {

    private final UpbitClient upbitClient;

    @Override
    public ProcessResultOrder order(OrderType orderType) {
        log.info("upbit excute : order {}", orderType);
        return null;
    }

    @Override
    public ProcessResultOrderCancel orderCancel() {
        log.info("upbit excute : order cancel");
        return null;
    }

    @Override
    public ProcessResultAccount getAccount() {
        log.info("upbit excute : get account");
        return null;
    }

    @Override
    public ProcessResultOrderInfo getOrderInfo() {
        log.info("upbit execute : get order info");
        return null;
    }

    @Override
    public CoinExchangeType getExchangeType() {
        return CoinExchangeType.UPBIT;
    }
}
