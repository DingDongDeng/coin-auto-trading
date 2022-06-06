package com.dingdongdeng.coinautotrading.trading.exchange.common;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import java.time.LocalDateTime;

public interface ExchangeCandleService {

    /*
     * start를 기준으로 최대 캔들 N개까지 조회 가능
     * start < 캔들 <= end 범위로 조회
     * start ~ end 순서로 정렬된 캔들 정보 반환
     * start가 null이라면 end를 기준으로 캔들 N개까지 조회
     *
     * N : 거래소 별로 정해져 있는 캔들 최대 조회 개수
     */
    ExchangeCandles getCandles(CoinType coinType, CandleUnit candleUnit, LocalDateTime start, LocalDateTime end, String keyPairId);

    CoinExchangeType getCoinExchangeType();

}
