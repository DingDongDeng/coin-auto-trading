package com.dingdongdeng.coinautotrading.common.type;

import java.util.EnumMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradingTerm {
    EXTREME_SCALPING("극한의 스캘핑(1분봉 거래)", CandleUnit.UNIT_1M),
    SCALPING_15M("스캘핑(15분봉 거래)", CandleUnit.UNIT_15M),
    SCALPING_60M("스캘핑(60분봉 거래)", CandleUnit.UNIT_60M),
    SCALPING_240M("스캘핑(240분봉 거래)", CandleUnit.UNIT_240M),
    DAY("데이 (일봉 거래)", CandleUnit.UNIT_1D),
    SWING("스윙 (주봉 거래)", CandleUnit.UNIT_1W),
    ;

    private String desc;
    private CandleUnit candleUnit; // 참조할 차트의 캔들 사이즈(5분 봉, 15분 봉,

    public static EnumMap<TradingTerm, String> toMap() {
        EnumMap<TradingTerm, String> map = new EnumMap<>(TradingTerm.class);
        for (TradingTerm value : TradingTerm.values()) {
            map.put(value, value.getDesc());
        }
        return map;
    }
}
