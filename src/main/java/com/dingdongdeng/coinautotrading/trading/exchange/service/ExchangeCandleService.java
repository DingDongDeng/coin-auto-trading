package com.dingdongdeng.coinautotrading.trading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;

public interface ExchangeCandleService {

    ExchangeCandles getCandleList(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId);

    CoinExchangeType getCoinExchangeType();
}
