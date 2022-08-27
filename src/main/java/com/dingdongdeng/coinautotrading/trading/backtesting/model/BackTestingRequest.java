package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

public class BackTestingRequest {

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {

        @NotNull
        private String autoTradingProcessorId;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
        private LocalDateTime start;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
        private LocalDateTime end;
        @NotNull
        private CandleUnit baseCandleUnit;
    }

}
