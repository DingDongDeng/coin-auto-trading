package com.dingdongdeng.coinautotrading.trading.exchange.service;

//import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitExchangeCandleServiceTest {

    @Autowired
    private UpbitExchangeCandleService service;

    @Test
    public void 캔들_1분봉_조회_테스트() {
        LocalDateTime start = LocalDateTime.of(2022, 01, 13, 21, 9, 0);
        LocalDateTime end = LocalDateTime.of(2022, 01, 13, 21, 19, 0);
        log.info("start : {}", start);
        log.info("end : {}", end);
        ExchangeCandles candles = service.getCandleList(CoinType.ETHEREUM, CandleUnit.UNIT_1M, start, end);
        log.info("size : {}, candles: {}", candles.getCandleList().size(), candles);

        assertEquals(9, candles.getCandleList().size());
    }

}