package com.dingdongdeng.coinautotrading.exchange.component;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexCalculator {

    private final int RSI_STANDARD_PERIOD = 14;

    public double getRsi(CoinExchangeType coinExchangeType) {
        double AU = 0d;
        double AD = 0d;
        return 0d;
    }
}
