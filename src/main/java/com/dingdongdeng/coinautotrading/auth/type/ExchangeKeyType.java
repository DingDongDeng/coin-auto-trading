package com.dingdongdeng.coinautotrading.auth.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeKeyType {
  ACCESS_KEY("액세스 키"),
  SECRET_KEY("시크릿 키"),
  ;

  private String desc;
}
