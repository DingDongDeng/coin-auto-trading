package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradingTag {
  PROFIT("익절"),
  LOSS("손절"),
  BUY("매수");

  private String desc;
}
