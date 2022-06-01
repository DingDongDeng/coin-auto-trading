package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCoreParam;
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
    ;

    private String desc;
    private MarketType marketType;
    private Class<? extends StrategyCore> strategyCoreClazz;
    private Class<? extends StrategyCoreParam> strategyCoreParamClazz;

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

    public StrategyCore getStrategyCore(StrategyCoreParam param) {
        try {
            return strategyCoreClazz.getDeclaredConstructor(strategyCoreParamClazz).newInstance(param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
