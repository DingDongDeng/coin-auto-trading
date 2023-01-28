package com.dingdongdeng.coinautotrading.trading.index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
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
        @JsonIgnore
        private double[] hists;
        @JsonIgnore
        private double[] signals;
        @JsonIgnore
        private double[] macds;

        public double getLatestHist(int index) {
            return hists[hists.length - 1 - index];
        }

        public double getLatestMacd(int index) {
            return macds[macds.length - 1 - index];
        }

        public double getCurrentUptrendHighestHist() {
            return this.getCurrentUptrendHighest(hists);
        }

        public double getCurrentDowntrendLowestHist() {
            return this.getCurrentDowntrendLowest(hists);
        }

        public double getCurrentUptrendHighestMacd() {
            int offset = macds.length - 1;
            for (int i = hists.length - 1; i >= 0; i--) {
                if (hists[i] < 0) {
                    break;
                }
                offset--;
            }
            return this.getCurrentUptrendHighest(Arrays.copyOfRange(macds, offset, macds.length));
        }

        public double getCurrentUptrendLowestMacd() {
            return this.getCurrentUptrendHighest(macds);
        }

        public double getRecentUptrendAverageMacd(int recent) {
            double sum = 0;
            int downtrendCount = 0;
            for (int i = 0; i < recent; i++) {
                double macd = macds[macds.length - 1 - i];
                if (macd < 0) {
                    downtrendCount++;
                    continue;
                }
                sum += macd;
            }
            double total = (recent - downtrendCount);
            return sum / total == 0 ? 1 : total;
        }

        private double getCurrentUptrendHighest(double[] out) {
            double highest = 0;
            for (int i = out.length - 1; i >= 0; i--) {
                if (out[i] < 0) {
                    break;
                }
                if (out[i] >= highest) {
                    highest = out[i];
                }
            }
            return highest;
        }

        private double getCurrentDowntrendLowest(double[] out) {
            double lowest = 0;
            for (int i = out.length - 1; i >= 0; i--) {
                if (out[i] > 0) {
                    break;
                }
                if (out[i] <= lowest) {
                    lowest = out[i];
                }
            }
            return lowest;
        }
    }
}
