package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FutureTradingResult implements TradingResult {

    private String identifyCode;
    private CoinType coinType;
    private TradingTerm tradingTerm;
    private OrderType orderType;
    private OrderState orderState;
    private Double volume;
    private Double price;
    private Double fee;
    private PriceType priceType;
    private String orderId;
    private TradingTag tradingTag;
    private LocalDateTime createdAt;


    public boolean isDone() {
        return this.orderState == OrderState.DONE;
    }
}