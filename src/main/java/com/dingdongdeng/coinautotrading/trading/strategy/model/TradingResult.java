package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.time.LocalDateTime;
import java.util.Objects;
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
    private String identifyCode;
    private CoinType coinType;
    private TradingTerm tradingTerm;
    private OrderType orderType;
    private OrderState orderState;
    private Double volume;
    private Double price;
    private PriceType priceType;
    private String orderId;
    private TradingTag tag;
    private LocalDateTime createdAt;

    public boolean isDone() {
        return this.orderState == OrderState.DONE;
    }

    public boolean isExist() {
        return Objects.nonNull(this.id);
    }

}
