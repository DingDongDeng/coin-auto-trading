package com.dingdongdeng.coinautotrading.trading.index;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Index {

    private double rsi;
    private List<Double> resistancePriceList;
    private Macd macd;

    @ToString
    @Getter
    @Builder
    public static class Macd {

        private double hist;
        private double signal;
        private double macd;
        private double currentUptrendHighestHist; // 현재 상승 추세에서 가장 높은 hist (상승 추세가 아니라면 0)
        private double currentDowntrendLowestHist; // 현재 하락 추세에서 가장 낮은 hist (하락 추세가 아니라면 0)
        private double[] hists;

        public double getLatestHist(int index) {
            return hists[hists.length - 1 - index];
        }
    }
}
