package com.dingdongdeng.coinautotrading.upbit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UpbitRequest {

    @ToString
    @Getter
    @Builder
    public static class OrderChanceRequest {

        @JsonProperty("market")
        private String market; // 마켓 ID
    }

    @ToString
    @Getter
    @Builder
    public static class MarketCodeRequest {

        @JsonProperty("isDetail")
        private boolean isDetail; // 유의종목 필드과 같은 상세 정보 노출 여부
    }
}
