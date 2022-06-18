package com.dingdongdeng.coinautotrading.trading.strategy.core;

import com.dingdongdeng.coinautotrading.trading.strategy.annotation.GuideMessage;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreFutureParam;
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
public class TestStrategyCoreParam implements StrategyCoreFutureParam {

    @GuideMessage("사용할 레버러지")
    private int leverage;

    @GuideMessage("태스트를 위한 메세지")
    private String msg;

}
