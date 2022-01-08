package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfoParam;

public interface ExchangeProcessor {

    ProcessOrder order(ProcessOrderParam param);

    ProcessOrderCancel orderCancel(ProcessOrderCancelParam param);

    ProcessTradingInfo getTradingInformation(ProcessTradingInfoParam param);

    CoinExchangeType getExchangeType();
}
