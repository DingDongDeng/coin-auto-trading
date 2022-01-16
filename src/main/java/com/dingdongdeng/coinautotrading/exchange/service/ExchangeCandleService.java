package com.dingdongdeng.coinautotrading.exchange.service;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeCandleService {

    void store(List<CoinType> coinTypeList, LocalDateTime start, LocalDateTime end);
}
