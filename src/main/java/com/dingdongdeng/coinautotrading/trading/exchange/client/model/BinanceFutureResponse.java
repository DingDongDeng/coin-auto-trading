package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BinanceFutureResponse {

  @ToString
  @Getter
  public static class FutureAccountBalanceResponse {

    @JsonProperty("accountAlias")
    private String accountAlias; // 고유 계정 코드

    @JsonProperty("asset")
    private String asset; // 자산명

    @JsonProperty("balance")
    private String balance; // 지갑 잔고

    @JsonProperty("crossWalletBalance")
    private String crossWalletBalance; // 교차 지갑 잔고

    @JsonProperty("crossUnPnl")
    private String crossUnPnl; // 교차 직위의 미실현 이익

    @JsonProperty("availableBalance")
    private String availableBalance; // 사용가능잔액

    @JsonProperty("maxWithdrawAmount")
    private String maxWithdrawAmount; // 송금 한도액

    @JsonProperty("marginAvailable")
    private boolean marginAvailable; // 자산을 다중 자산 모드에서 여백으로 사용할 수 있는지 여부

    @JsonProperty("updateTime")
    private Long updateTime; //
  }
}
