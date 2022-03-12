package com.dingdongdeng.coinautotrading.trading.backtesting.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BackTestingRequest {

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {

        @NotNull
        private String autoTradingProcessorId;

    }

}
