package com.dingdongdeng.coinautotrading.trading.exchange.spot.service;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model.SpotExchangeCandles;
import java.time.LocalDateTime;

public interface SpotExchangeCandleService {

    SpotExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId);

    CoinExchangeType getCoinExchangeType();

}
