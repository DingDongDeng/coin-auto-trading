package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class BackTestingRequest {

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {

        @NotNull
        private String autoTradingProcessorId;
        @NotNull
        private LocalDateTime start;
        @NotNull
        private LocalDateTime end;

    }

}
