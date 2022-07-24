package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.PrototypeFutureStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.PrototypeFutureStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiV2StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiV2StrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyCode {
    SCALE_TRADING_RSI("분할 매수(RSI기반, 손절없음)", MarketType.SPOT, ScaleTradingRsiStrategyCore.class, ScaleTradingRsiStrategyCoreParam.class),
    SCALE_TRADING_RESISTANCE("분할 매수(저항선기반, 손절있음)", MarketType.SPOT, ScaleTradingRsiV2StrategyCore.class, ScaleTradingRsiV2StrategyCoreParam.class),
    TEST_FUTURE("테스트 선물 전략", MarketType.FUTURE, PrototypeFutureStrategyCore.class, PrototypeFutureStrategyCoreParam.class),
    ;

    private final String desc;
    private final MarketType marketType;
    private final Class<? extends StrategyCore<?, ?>> strategyCoreClazz;
    private final Class<? extends StrategyCoreParam> strategyCoreParamClazz;

    public static StrategyCode of(String name) {
        return Arrays.stream(StrategyCode.values())
            .filter(type -> type.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(name));
    }

    public static EnumMap<StrategyCode, String> toMap() {
        EnumMap<StrategyCode, String> map = new EnumMap<>(StrategyCode.class);
        for (StrategyCode value : StrategyCode.values()) {
            map.put(value, value.getDesc());
        }
        return map;
    }

    public StrategyCore<?, ?> getStrategyCore(StrategyCoreParam param) {
        try {
            return strategyCoreClazz.getDeclaredConstructor(strategyCoreParamClazz).newInstance(param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
