package com.dingdongdeng.coinautotrading.trading.index;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class Resistance {

    private List<Double> resistancePriceList;

    public double getSupportPrice(double standardPrice) {
        int nextCount = 0;
        for (int i = this.resistancePriceList.size() - 1; i >= 0; i--) {
            double supportPrice = this.resistancePriceList.get(i);
            if (standardPrice > supportPrice) {
                //if (nextCount < next) {
                //    nextCount++;
                //    continue;
                //}
                return supportPrice;
            }

        }
        log.warn("지지선 찾지 못함");
        return 0;
    }

    public double getResistancePrice(double standardPrice) {
        for (Double resistancePrice : this.resistancePriceList) {
            if (standardPrice < resistancePrice) {
                return resistancePrice;
            }
        }
        log.warn("저항선 찾지 못함");
        return Integer.MAX_VALUE;
    }
}
