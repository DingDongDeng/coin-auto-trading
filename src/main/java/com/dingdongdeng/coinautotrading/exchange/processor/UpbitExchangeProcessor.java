package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitExchangeProcessor implements ExchangeProcessor {

    private final UpbitClient upbitClient;

    @Override
    public ProcessOrderResult order(ProcessOrderParam param) {
        log.info("upbit execute : order ");
        return null;
    }

    @Override
    public ProcessOrderCancelResult orderCancel(ProcessOrderCancelParam param) {
        log.info("upbit execute : order cancel");
        return null;
    }

    @Override
    public ProcessAccountResult getAccount(ProcessAccountParam param) {
        log.info("upbit execute : get account");
        return null;
    }

    @Override
    public ProcessOrderInfoResult getOrderInfo(ProcessOrderInfoParam param) {
        log.info("upbit execute : get order info");
        return null;
    }

    @Override
    public CoinExchangeType getExchangeType() {
        return CoinExchangeType.UPBIT;
    }
}
