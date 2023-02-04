package com.dingdongdeng.coinautotrading.trading.strategy.model.type;

import com.dingdongdeng.coinautotrading.common.type.MarketType;
import com.dingdongdeng.coinautotrading.trading.strategy.StrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.BBandsTradingStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.BBandsTradingStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.PrototypeFutureStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.PrototypeFutureStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.core.TrendSwitchTradingStrategyCore;
import com.dingdongdeng.coinautotrading.trading.strategy.core.TrendSwitchTradingStrategyCoreParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.StrategyCoreParam;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyCode {
    TREND_SWITCH_TRADING("추세 구분 매매(240분봉 추천)", MarketType.SPOT, TrendSwitchTradingStrategyCore.class, TrendSwitchTradingStrategyCoreParam.class),
    BBANDS_TRADING("볼링저밴드 기반 매매(240분봉 추천)", MarketType.SPOT, BBandsTradingStrategyCore.class, BBandsTradingStrategyCoreParam.class),
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
