package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessCandleParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessCandleResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;

public interface ExchangeProcessor {

    //fixme Strategy 작성 후, 불필요한 필드들 result에서 제거해서 최적화해야함
    ProcessOrderResult order(ProcessOrderParam param);

    ProcessOrderCancelResult orderCancel(ProcessOrderCancelParam param);

    ProcessAccountResult getAccount(ProcessAccountParam param);

    ProcessOrderInfoResult getOrderInfo(ProcessOrderInfoParam param);

    ProcessCandleResult getCandleList(ProcessCandleParam param);

    CoinExchangeType getExchangeType();
}
