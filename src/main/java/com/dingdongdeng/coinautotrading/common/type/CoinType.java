package com.dingdongdeng.coinautotrading.common.type;

import java.util.EnumMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinType {
  ADA("에이다"),
  SOLANA("솔라나"),
  XRP("리플"),
  ETHEREUM("이더리움"),
  AVALANCHE("아발란체"),
  POLKADOT("폴카닷"),
  DOGE("도지"),
  BITCOIN("비트코인"),
  ;

  private String desc;

  public static EnumMap<CoinType, String> toMap() {
    EnumMap<CoinType, String> map = new EnumMap<>(CoinType.class);
    for (CoinType value : CoinType.values()) {
      map.put(value, value.getDesc());
    }
    return map;
  }
}
