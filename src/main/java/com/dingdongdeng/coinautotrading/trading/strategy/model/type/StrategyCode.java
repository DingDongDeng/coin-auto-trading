package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.RsiStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.RsiStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCore2;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.ScaleTradingRsiStrategyCoreParam2;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyCode {
    RSI("RSI 지표 기반 매매", RsiStrategyCore.class, RsiStrategyCoreParam.class),
    SCALE_TRADING_RSI("RSI 지표 기반 물타기 매매", ScaleTradingRsiStrategyCore.class, ScaleTradingRsiStrategyCoreParam.class),
    SCALE_TRADING_RSI2("RSI 지표 기반 물타기 매매(분할 손절)", ScaleTradingRsiStrategyCore2.class, ScaleTradingRsiStrategyCoreParam2.class),
    ;

    private String desc;
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
