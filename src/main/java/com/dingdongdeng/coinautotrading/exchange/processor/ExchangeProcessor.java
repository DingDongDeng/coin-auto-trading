package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessedTradingInfo;

public interface ExchangeProcessor {

    ProcessedOrder order(ProcessOrderParam param);

    ProcessedOrderCancel orderCancel(ProcessOrderCancelParam param);

    ProcessedTradingInfo getTradingInformation(ProcessTradingInfoParam param);

    CoinExchangeType getExchangeType();
}
