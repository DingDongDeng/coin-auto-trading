package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;

public interface ExchangeCandleService {

    ExchangeCandles getCandleList(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end);
}
