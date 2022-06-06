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
