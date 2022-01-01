package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;

public interface ExchangeProcessor {

    ProcessOrderResult order(ProcessOrderParam param);

    ProcessOrderCancelResult orderCancel(ProcessOrderCancelParam param);

    ProcessAccountResult getAccount(ProcessAccountParam param);

    ProcessOrderInfoResult getOrderInfo(ProcessOrderInfoParam param);

    CoinExchangeType getExchangeType();
}
