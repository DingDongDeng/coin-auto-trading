package com.dingdongdeng.coinautotrading.trading.index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class BollingerBands {

    private double upper;
    private double middle;
    private double lower;
    private double height;
    private double heightHist;

    @JsonIgnore
    private double[] lowers;

    // 현재 시점으로부터 n번째 과거 캔들을 조회
    public double getLatestLower(int index) {
        if (lowers.length < index + 1) {
            throw new RuntimeException("Not found candle");
        }
        return lowers[(lowers.length - 1 - index)];
    }
}
