package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.backtesting.model.type.BackTestingProcessStatus;
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
public class BackTestingResponse {

    private String backTestingId;
    private String userId;
    private String autoTradingProcessorId;
    private Result result;

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {

        private BackTestingProcessStatus status;
        private Double marginPrice;
        private Double marginRate;
    }
}
