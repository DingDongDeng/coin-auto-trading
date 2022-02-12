package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("tradingResult")
public class TradingResult {

    @Id
    @Setter
    private String id;
    private String strategyName;
    private CoinType coinType;
    private OrderType orderType;
    private Double volume;
    private Double price;
    private PriceType priceType;
    private String orderId;
    private TradingTag tag;

}
