package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeTicker {

    private String symbol;
    private Double markPrice;
    private Double indexPrice;
    private Double estimatedSettlePrice;
    private Double lastFundingRate;
    private Double interestRate;
    private Long nextFundingTime;
    private Long timestamp;  //타임스탬프

}
