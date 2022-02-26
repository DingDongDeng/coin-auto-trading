package com.dingdongdeng.coinautotrading.trading.exchange.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExchangeCandleParam {

    private Integer unit; // 분 단위. 가능한 값 : 1, 3, 5, 15, 10, 30, 60, 240
    private CoinType coinType; // 마켓 ID (필수)
    private LocalDateTime to; // 마지막 캔들 시각
    private Integer count; // 캔들 개수
}
