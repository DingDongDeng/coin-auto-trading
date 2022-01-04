package com.dingdongdeng.coinautotrading.autotrading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class OrderTask {

    private CoinType coinType;
    private OrderType orderType;
    private Double volume;
    private Double price;
    private PriceType priceType;

}
