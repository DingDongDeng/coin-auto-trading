package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradingTerm {
    SCALPING("스캘핑", CandleUnit.UNIT_15M),
    DAY("데이", CandleUnit.UNIT_1D),
    SWING("스윙", CandleUnit.UNIT_1W),
    ;

    private String desc;
    private CandleUnit candleUnit; // 참조할 차트의 캔들 사이즈(5분 봉, 15분 봉,
}
