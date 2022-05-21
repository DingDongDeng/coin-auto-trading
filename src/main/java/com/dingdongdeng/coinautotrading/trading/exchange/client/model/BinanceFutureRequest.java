package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BinanceFutureRequest {

    @ToString
    @Getter
    @Builder
    public static class FuturesAccountBalanceRequest {

        @JsonProperty("timestamp")
        private Long timestamp;

    }
}
