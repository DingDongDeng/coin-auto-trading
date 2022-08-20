package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.trading.backtesting.model.type.BackTestingProcessStatus;
import java.time.LocalDateTime;
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
    private String recorderId;
    private String userId;
    private String autoTradingProcessorId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Result result;

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {

        private BackTestingProcessStatus status;
        private double executionRate;
        private double marginPrice;
        private Double marginRate;
        private double totalFee;
        private String eventMessage;
    }
}
