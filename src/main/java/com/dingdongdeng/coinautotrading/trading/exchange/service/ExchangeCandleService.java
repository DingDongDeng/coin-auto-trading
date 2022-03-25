package com.dingdongdeng.coinautotrading.trading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;

public interface ExchangeCandleService {
    ExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, int count, String keyPairId);

    CoinExchangeType getCoinExchangeType();

}
