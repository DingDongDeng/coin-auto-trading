package com.dingdongdeng.coinautotrading.trading.backtesting.model.type;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BackTestingExchangeFeeType {
    UPBIT(CoinExchangeType.UPBIT, 0.05),
    BINANCE(CoinExchangeType.BINANCE_FUTURE, 0.04), //fixme 선물의 경우 레버리지 개념때문에 수수료 공식이 따로 적용되어야함
    ;

    private CoinExchangeType coinExchangeType;
    private double feeRate;

    public static BackTestingExchangeFeeType of(CoinExchangeType coinExchangeType) {
        return Arrays.stream(BackTestingExchangeFeeType.values())
            .filter(type -> type.getCoinExchangeType() == coinExchangeType)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(coinExchangeType.name()));
    }
}
