package com.dingdongdeng.coinautotrading.exchange.upbit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderRequest {

        @JsonProperty("market")
        private String market; // 마켓 ID (필수)
        @JsonProperty("side")
        private String side; // 주문 종류 (필수) - bid : 매수 - ask : 매도
        @JsonProperty("volume")
        private Double volume; // 주문량 (지정가, 시장가 매도 시 필수)
        @JsonProperty("price")
        private Double price; // 주문 가격. (지정가, 시장가 매수 시 필수)  ex) KRW-BTC 마켓에서 1BTC당 1,000 KRW로 거래할 경우, 값은 1000 이 된다. ex) KRW-BTC 마켓에서 1BTC당 매도 1호가가 500 KRW 인 경우, 시장가 매수 시 값을 1000으로 세팅하면 2BTC가 매수된다. (수수료가 존재하거나 매도 1호가의 수량에 따라 상이할 수 있음)
        @JsonProperty("ord_type")
        private String ordType; // 주문 타입 (필수) - limit : 지정가 주문 - price : 시장가 주문(매수) - market : 시장가 주문(매도)
        @JsonProperty("identifier")
        private String identifier; // 조회용 사용자 지정값 (선택) (Uniq 값 사용)
    }

}
