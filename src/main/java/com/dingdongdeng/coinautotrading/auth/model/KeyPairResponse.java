package com.dingdongdeng.coinautotrading.auth.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyPairResponse {

  private String pairId;
  private CoinExchangeType coinExchangeType;
  private List<Key> keyList;
}
